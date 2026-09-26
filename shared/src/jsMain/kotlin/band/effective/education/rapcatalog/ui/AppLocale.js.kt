package band.effective.education.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Web (Kotlin/JS).
 *
 * Локаль браузера — `navigator.language`, и она только на чтение: страница не может
 * сказать браузеру, что теперь она русская. Поэтому кнопка языка меняет composition
 * local, но подписи остаются на локали браузера.
 *
 * Веха ведётся и проверяется на desktop, web здесь бонус. Честный путь для web —
 * своя таблица подписей вместо системной локали; это уже В3, где появляются
 * ресурсы через три таргета.
 */
actual object PlatformLocale {
    private val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

    @Composable
    actual fun provides(language: AppLanguage): ProvidedValue<*> =
        LocalAppLanguage provides language
}
