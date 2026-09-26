package band.effective.education.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
enum class AppLanguage(val code: String) {
    RU("ru"),
    EN("en"),
    ;

    fun next(): AppLanguage = entries[(ordinal + 1) % entries.size]
}

expect object PlatformLocale {
    @Composable
    fun provides(language: AppLanguage): ProvidedValue<*>
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

@Composable
fun AppLocale(language: AppLanguage, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        PlatformLocale.provides(language),
        LocalAppLanguage provides language,
        content = content,
    )
}
