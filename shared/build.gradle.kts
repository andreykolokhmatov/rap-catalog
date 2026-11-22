import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.StringReader
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

// Адреса API и User-Agent живут в api.properties в корне, а не в коде. Ключ из
// local.properties с префиксом api. (например, api.musicbrainz.url) перекрывает
// значение из api.properties — так подставляется зеркало или тестовый сервер.
// Из них генерируется объект ApiConfig в build/generated, в git он не попадает.
abstract class GenerateApiConfig : DefaultTask() {
    @get:Input
    abstract val values: MapProperty<String, String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val pkg = "ru.omgtu.kolokhmatov.rapcatalog.data.network"
        val constants = values.get().toSortedMap().entries.joinToString("\n") { (key, value) ->
            val name = key.uppercase().replace('.', '_').replace('-', '_')
            require(name.matches(Regex("[A-Z][A-Z0-9_]*"))) { "Ключ api.properties не годится в имя константы: $key" }
            val literal = value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("$", "\\$")
                .replace("\n", "\\n")
            "    const val $name = \"$literal\""
        }
        val file = outputDir.file("${pkg.replace('.', '/')}/ApiConfig.kt").get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            "// Сгенерировано задачей generateApiConfig из api.properties. Не править.\n" +
                "package $pkg\n\ninternal object ApiConfig {\n$constants\n}\n",
        )
    }
}

fun readProperties(name: String): Map<String, String> {
    val text = providers.fileContents(rootProject.layout.projectDirectory.file(name)).asText.orNull
        ?: return emptyMap()
    val properties = Properties().apply { load(StringReader(text)) }
    return properties.stringPropertyNames().associateWith(properties::getProperty)
}

val generateApiConfig by tasks.registering(GenerateApiConfig::class) {
    val api = readProperties("api.properties")
    require(api.isNotEmpty()) { "Нет api.properties в корне проекта: адреса API берутся оттуда" }
    val overrides = readProperties("local.properties")
        .filterKeys { it.startsWith("api.") }
        .mapKeys { it.key.removePrefix("api.") }
    values.set(api + overrides)
    outputDir.set(layout.buildDirectory.dir("generated/apiConfig"))
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm {
        compilerOptions {
            moduleName = "shared"
        }
    }
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    android {
       namespace = "ru.omgtu.kolokhmatov.rapcatalog.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
           moduleName = "shared"
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.navigation3.runtime)
            implementation(libs.navigation3.ui)
            implementation(libs.kotlinx.coroutinesCore)
            implementation(libs.androidx.lifecycle.viewmodelNavigation3)
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.ktor.clientCore)
            implementation(libs.ktor.clientContentNegotiation)
            implementation(libs.ktor.serializationKotlinxJson)
            implementation(libs.coil.compose)
            implementation(libs.coil.networkKtor)
            implementation(libs.okio)
        }
        commonMain {
            kotlin.srcDir(generateApiConfig.map { it.outputDir })
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutinesTest)
            implementation(libs.ktor.clientMock)
        }
        androidMain.dependencies {
            implementation(libs.ktor.clientOkhttp)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.clientJava)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(libs.ktor.clientJs)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.clientJs)
        }
        iosMain.dependencies {
            implementation(libs.ktor.clientDarwin)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
    // Доменные модели неизменяемы, но лежат вне UI и без аннотаций Compose.
    // Конфиг объявляет их стабильными, чтобы экраны остались skippable.
    stabilityConfigurationFiles.add(layout.projectDirectory.file("compose-stability.conf"))
}

compose.resources {
    // Res генерируется internal, и подписи не видны из модулей-точек входа —
    // заголовок окна desktop без этого не сделать ресурсом.
    publicResClass = true
    packageOfResClass = "ru.omgtu.kolokhmatov.rapcatalog.resources"
}