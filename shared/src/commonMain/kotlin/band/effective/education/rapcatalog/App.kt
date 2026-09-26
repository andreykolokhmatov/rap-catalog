package band.effective.education.rapcatalog

import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import band.effective.education.rapcatalog.data.ArtistRepositoryImpl
import band.effective.education.rapcatalog.detail.ArtistDetailViewModelFactory
import band.effective.education.rapcatalog.domain.ArtistRepository
import band.effective.education.rapcatalog.list.ArtistListViewModelFactory
import band.effective.education.rapcatalog.resources.Res
import band.effective.education.rapcatalog.resources.action_switch_language
import band.effective.education.rapcatalog.resources.action_toggle_theme
import band.effective.education.rapcatalog.resources.language_short
import band.effective.education.rapcatalog.ui.AppLanguage
import band.effective.education.rapcatalog.ui.AppLocale
import band.effective.education.rapcatalog.ui.AppTheme
import band.effective.education.rapcatalog.ui.components.AppScaffold
import band.effective.education.rapcatalog.ui.components.LanguageFrame
import band.effective.education.rapcatalog.ui.components.ThemeIcon
import band.effective.education.rapcatalog.ui.navigation.AppNavDisplay
import band.effective.education.rapcatalog.ui.navigation.Navigator
import org.jetbrains.compose.resources.stringResource

/**
 * Корень приложения: тема, язык, зависимости и навигация — по одному разу и здесь.
 *
 * И тема, и язык живут в памяти сессии: закрыли окно — вернулись к значениям по
 * умолчанию. Сохранять их на диск в этой вехе нельзя, локальный стор это В3.
 */
@Composable
fun App() {
    // Тёмная тема по умолчанию: профиль продукта «музыкальный каталог» —
    // тёмный фон, на котором цветные метки жанров работают как данные.
    var darkTheme by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf(AppLanguage.RU) }

    AppTheme(darkTheme) {
        AppLocale(language) {
            // Один репозиторий на всё приложение: оба экрана читают одни и те же данные.
            val repository: ArtistRepository = remember { ArtistRepositoryImpl() }
            val navigator = remember { Navigator() }
            // Здесь собираются зависимости всего приложения; экраны получают готовые
            // фабрики и не знают, из чего сделаны их ViewModel.
            val listViewModelFactory = remember { ArtistListViewModelFactory(navigator, repository) }
            val detailViewModelFactory =
                remember { ArtistDetailViewModelFactory(navigator, repository) }

            val navStack = navigator.navStack.collectAsStateWithLifecycle()
            val canGoBack by remember { derivedStateOf { navStack.value.size > 1 } }

            val themeLabel = stringResource(Res.string.action_toggle_theme)
            val languageLabel = stringResource(Res.string.action_switch_language)

            AppScaffold(
                onBack = navigator::back.takeIf { canGoBack },
                actions = {
                    IconButton(
                        onClick = { language = language.next() },
                        modifier = Modifier.semantics { contentDescription = languageLabel },
                    ) {
                        // Подпись показывает язык, на который переключит нажатие.
                        LanguageFrame {
                            Text(
                                text = stringResource(Res.string.language_short),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                    IconButton(
                        onClick = { darkTheme = !darkTheme },
                        modifier = Modifier.semantics { contentDescription = themeLabel },
                    ) {
                        ThemeIcon(darkTheme = darkTheme)
                    }
                },
            ) { modifier ->
                AppNavDisplay(
                    modifier = modifier,
                    navigator = navigator,
                    listViewModelFactory = listViewModelFactory,
                    detailViewModelFactory = detailViewModelFactory,
                )
            }
        }
    }
}
