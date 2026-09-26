package band.effective.education.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Web (Kotlin/Wasm). То же ограничение, что и в `AppLocale.js.kt`:
 * `navigator.language` только на чтение.
 */
actual object PlatformLocale {
    private val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

    @Composable
    actual fun provides(language: AppLanguage): ProvidedValue<*> =
        LocalAppLanguage provides language
}
