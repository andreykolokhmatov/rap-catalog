package band.effective.education.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

actual object PlatformLocale {
    private val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

    @Composable
    actual fun provides(language: AppLanguage): ProvidedValue<*> {
        Locale.setDefault(Locale.forLanguageTag(language.code))
        return LocalAppLanguage provides language
    }
}
