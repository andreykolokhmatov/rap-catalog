package band.effective.education.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/**
 * Desktop — ведущий таргет вехи, и здесь переключение языка работает целиком.
 *
 * `compose-resources` спрашивает локаль у платформы, на JVM это
 * `java.util.Locale.getDefault()`. Меняем её и отдаём язык статическим
 * composition local — поддерево перерисовывается и перечитывает подписи.
 */
actual object PlatformLocale {
    private val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

    @Composable
    actual fun provides(language: AppLanguage): ProvidedValue<*> {
        Locale.setDefault(Locale.forLanguageTag(language.code))
        return LocalAppLanguage provides language
    }
}
