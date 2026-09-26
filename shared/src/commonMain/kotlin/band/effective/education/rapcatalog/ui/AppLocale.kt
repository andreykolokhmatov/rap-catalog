package band.effective.education.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Языки интерфейса. Коды совпадают с суффиксами папок в `composeResources`:
 * [RU] — `values`, [EN] — `values-en`.
 *
 * Список закрытый: добавить язык — добавить константу и папку, после чего
 * `tools/check-strings.py` сразу скажет, каких ключей в новой локали не хватает.
 */
@Immutable
enum class AppLanguage(val code: String) {
    RU("ru"),
    EN("en"),
    ;

    /** Следующий язык по кругу. Кнопка в шапке дёргает именно его. */
    fun next(): AppLanguage = entries[(ordinal + 1) % entries.size]
}

/**
 * Подмена языка, которым `compose-resources` выбирает файл подписей.
 *
 * Окружение ресурсов (`ComposeEnvironment`) у Compose закрыто — `internal`, руками
 * его не собрать. Зато окружение по умолчанию читает локаль платформы, а её на
 * каждом таргете меняют по-своему: отсюда `expect`/`actual`.
 *
 * Значение отдаётся через `staticCompositionLocalOf`: смена такого значения
 * перерисовывает поддерево целиком, и подписи успевают перечитаться с новой локалью.
 *
 * Выбор живёт в памяти сессии и на диск не пишется — диск это В3.
 */
expect object PlatformLocale {
    @Composable
    fun provides(language: AppLanguage): ProvidedValue<*>
}

/**
 * Выбранный язык, доступный любому экрану ниже.
 *
 * Нужен там, где текст не подпись интерфейса, а данные: справка об артисте есть
 * и на русском, и на английском, и выбрать между ними должен экран — через
 * `composeResources` это не проходит, там лежат только подписи.
 */
val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

/** Оборачивает поддерево в выбранный язык. Ставится один раз, в [band.effective.education.rapcatalog.App]. */
@Composable
fun AppLocale(language: AppLanguage, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        PlatformLocale.provides(language),
        LocalAppLanguage provides language,
        content = content,
    )
}
