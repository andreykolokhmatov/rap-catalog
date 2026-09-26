package band.effective.education.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSUserDefaults

/**
 * iOS. Список предпочитаемых языков лежит в `AppleLanguages`; Foundation читает его
 * при старте, поэтому подписи целиком переедут на новый язык после перезапуска.
 *
 * iOS в этой вехе бонус: без macOS он даже не собирается.
 */
actual object PlatformLocale {
    private val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

    @Composable
    actual fun provides(language: AppLanguage): ProvidedValue<*> {
        NSUserDefaults.standardUserDefaults.setObject(listOf(language.code), "AppleLanguages")
        return LocalAppLanguage provides language
    }
}
