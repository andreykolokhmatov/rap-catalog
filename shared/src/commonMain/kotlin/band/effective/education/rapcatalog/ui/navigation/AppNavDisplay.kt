package band.effective.education.rapcatalog.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import band.effective.education.rapcatalog.Screen
import band.effective.education.rapcatalog.detail.ArtistDetailViewModel
import band.effective.education.rapcatalog.detail.ArtistDetailViewModelFactory
import band.effective.education.rapcatalog.list.ArtistListViewModel
import band.effective.education.rapcatalog.list.ArtistListViewModelFactory
import band.effective.education.rapcatalog.ui.screens.detail.ArtistDetailScreen
import band.effective.education.rapcatalog.ui.screens.list.ArtistListScreen

private const val TRANSITION_MS = 300

@Composable
fun AppNavDisplay(
    modifier: Modifier,
    navigator: Navigator,
    listViewModelFactory: ArtistListViewModelFactory,
    detailViewModelFactory: ArtistDetailViewModelFactory,
) {
    val backStack by navigator.navStack.collectAsStateWithLifecycle()
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { navigator.back() },
        transitionSpec = { slide(SlideDirection.Start) },
        popTransitionSpec = { slide(SlideDirection.End) },
        predictivePopTransitionSpec = { slide(SlideDirection.End) },
        entryProvider = entryProvider {
            entry<Screen.List> {
                val viewModel: ArtistListViewModel = viewModel(factory = listViewModelFactory)
                val state by viewModel.state.collectAsStateWithLifecycle()
                ArtistListScreen(state = state, onIntent = viewModel::onIntent)
            }
            entry<Screen.Detail> { key ->

                val viewModel: ArtistDetailViewModel = viewModel(
                    key = "detail-${key.id}",
                    factory = detailViewModelFactory,
                    extras = ArtistDetailViewModelFactory.extrasFor(key.id),
                )
                val state by viewModel.state.collectAsStateWithLifecycle()
                ArtistDetailScreen(state = state, onIntent = viewModel::onIntent)
            }
        },
    )
}

private fun AnimatedContentTransitionScope<*>.slide(
    direction: SlideDirection,
): ContentTransform =
    (slideIntoContainer(direction, tween(TRANSITION_MS)) + fadeIn(tween(TRANSITION_MS)))
        .togetherWith(
            slideOutOfContainer(direction, tween(TRANSITION_MS)) + fadeOut(tween(TRANSITION_MS)),
        )
