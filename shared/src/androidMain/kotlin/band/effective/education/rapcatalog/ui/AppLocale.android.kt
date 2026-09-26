package band.effective.education.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/**
 * Android. Локаль по умолчанию — тот же `java.util.Locale`, что и на desktop.
 *
 * Android в этой вехе бонусный: веха ведётся и проверяется на desktop.
 */
actual object PlatformLocale {
    private val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

    @Composable
    actual fun provides(language: AppLanguage): ProvidedValue<*> {
        Locale.setDefault(Locale.forLanguageTag(language.code))
        return LocalAppLanguage provides language
    }
}
