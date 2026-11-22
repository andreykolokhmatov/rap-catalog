package ru.omgtu.kolokhmatov.rapcatalog

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
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import ru.omgtu.kolokhmatov.rapcatalog.data.DataModule
import ru.omgtu.kolokhmatov.rapcatalog.ui.detail.ArtistDetailViewModelFactory
import ru.omgtu.kolokhmatov.rapcatalog.ui.list.ArtistListViewModelFactory
import ru.omgtu.kolokhmatov.rapcatalog.resources.Res
import ru.omgtu.kolokhmatov.rapcatalog.resources.action_switch_language
import ru.omgtu.kolokhmatov.rapcatalog.resources.action_toggle_theme
import ru.omgtu.kolokhmatov.rapcatalog.resources.language_short
import ru.omgtu.kolokhmatov.rapcatalog.ui.AppLanguage
import ru.omgtu.kolokhmatov.rapcatalog.ui.AppLocale
import ru.omgtu.kolokhmatov.rapcatalog.ui.AppTheme
import ru.omgtu.kolokhmatov.rapcatalog.ui.components.AppScaffold
import ru.omgtu.kolokhmatov.rapcatalog.ui.components.LanguageFrame
import ru.omgtu.kolokhmatov.rapcatalog.ui.components.ThemeIcon
import ru.omgtu.kolokhmatov.rapcatalog.ui.navigation.AppNavDisplay
import ru.omgtu.kolokhmatov.rapcatalog.ui.navigation.Navigator
import org.jetbrains.compose.resources.stringResource

@Composable
fun App() {
    val data = remember { DataModule() }
    // Фото грузит Coil через тот же HttpClient с движком своего таргета.
    // Кэш только в памяти: на диск в этой вехе ничего не пишется.
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(httpClient = { data.httpClient })) }
            .diskCache(null)
            .build()
    }

    var darkTheme by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf(AppLanguage.RU) }

    AppTheme(darkTheme) {
        AppLocale(language) {
            val navigator = remember { Navigator() }

            val listViewModelFactory =
                remember { ArtistListViewModelFactory(navigator, data.artistRepository) }
            val detailViewModelFactory =
                remember { ArtistDetailViewModelFactory(navigator, data.artistRepository) }

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
