package ru.omgtu.kolokhmatov.rapcatalog.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.resources.Res
import ru.omgtu.kolokhmatov.rapcatalog.resources.action_clear_search
import ru.omgtu.kolokhmatov.rapcatalog.resources.ic_close
import ru.omgtu.kolokhmatov.rapcatalog.resources.ic_search
import ru.omgtu.kolokhmatov.rapcatalog.resources.list_count
import ru.omgtu.kolokhmatov.rapcatalog.resources.list_empty
import ru.omgtu.kolokhmatov.rapcatalog.resources.list_found
import ru.omgtu.kolokhmatov.rapcatalog.resources.search_hint
import ru.omgtu.kolokhmatov.rapcatalog.ui.components.ArtistTile
import ru.omgtu.kolokhmatov.rapcatalog.ui.components.CardSurface
import ru.omgtu.kolokhmatov.rapcatalog.ui.components.ErrorMessage
import ru.omgtu.kolokhmatov.rapcatalog.ui.components.GenreChip
import ru.omgtu.kolokhmatov.rapcatalog.ui.components.LoadingIndicator
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val COLUMNS = 3

// Следующая страница просится, когда до конца списка осталось столько карточек.
private const val PREFETCH_DISTANCE = 6

private const val TYPE_HEADER = "header"
private const val TYPE_ARTIST = "artist"
private const val TYPE_FOOTER = "footer"

private val MAX_CONTENT_WIDTH = 1040.dp

@Composable
fun ArtistListScreen(
    state: ArtistListState,
    onIntent: (ArtistListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Column(modifier = Modifier.widthIn(max = MAX_CONTENT_WIDTH)) {
            SearchField(
                query = state.query,
                onQueryChange = { onIntent(ArtistListIntent.QueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp),
            )
            when (val content = state.content) {
                ArtistListContent.Loading -> LoadingIndicator(modifier = Modifier.fillMaxSize())
                ArtistListContent.NotFound -> EmptyResult(modifier = Modifier.fillMaxSize())
                is ArtistListContent.Error -> ErrorMessage(
                    reason = content.reason,
                    onRetry = { onIntent(ArtistListIntent.Retry) },
                    modifier = Modifier.fillMaxSize(),
                )
                is ArtistListContent.Loaded -> ArtistGrid(
                    content = content,
                    filtered = state.query.isNotBlank(),
                    onIntent = onIntent,
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        placeholder = { Text(stringResource(Res.string.search_hint)) },
        leadingIcon = { Icon(painterResource(Res.drawable.ic_search), contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        painterResource(Res.drawable.ic_close),
                        contentDescription = stringResource(Res.string.action_clear_search),
                    )
                }
            }
        },
    )
}

@Composable
private fun EmptyResult(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(32.dp), contentAlignment = Alignment.TopCenter) {
        Text(
            text = stringResource(Res.string.list_empty),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ArtistGrid(
    content: ArtistListContent.Loaded,
    filtered: Boolean,
    onIntent: (ArtistListIntent) -> Unit,
) {
    val gridState = rememberLazyGridState()
    EndReachedEffect(gridState, onEndReached = { onIntent(ArtistListIntent.EndReached) })

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(COLUMNS),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(COLUMNS) }, contentType = TYPE_HEADER) {
            val counter = if (filtered) Res.string.list_found else Res.string.list_count
            Text(
                text = stringResource(counter, content.total).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        items(items = content.artists, key = { it.id }, contentType = { TYPE_ARTIST }) { artist ->
            ArtistCard(
                artist = artist,
                onClick = { onIntent(ArtistListIntent.CardClicked(artist.id)) },
            )
        }

        if (content.nextPage != NextPageStatus.Idle) {
            item(span = { GridItemSpan(COLUMNS) }, contentType = TYPE_FOOTER) {
                NextPageFooter(
                    status = content.nextPage,
                    onRetry = { onIntent(ArtistListIntent.RetryNextPage) },
                )
            }
        }
    }
}

// Конец списка близко — просим следующую страницу. Число элементов в ключе:
// если после догрузки конец всё ещё виден, запрос уйдёт снова. Повтор во
// время загрузки и после ошибки отсекает ViewModel.
@Composable
private fun EndReachedEffect(gridState: LazyGridState, onEndReached: () -> Unit) {
    val currentOnEndReached by rememberUpdatedState(onEndReached)
    LaunchedEffect(gridState) {
        snapshotFlow {
            val layout = gridState.layoutInfo
            val last = layout.visibleItemsInfo.lastOrNull()?.index ?: 0
            val nearEnd = last >= layout.totalItemsCount - 1 - PREFETCH_DISTANCE
            layout.totalItemsCount to nearEnd
        }
            .distinctUntilChanged()
            .filter { (count, nearEnd) -> count > 0 && nearEnd }
            .collect { currentOnEndReached() }
    }
}

@Composable
private fun NextPageFooter(status: NextPageStatus, onRetry: () -> Unit) {
    when (status) {
        NextPageStatus.Loading -> LoadingIndicator(modifier = Modifier.fillMaxWidth(), size = 28.dp)
        is NextPageStatus.Error -> ErrorMessage(
            reason = status.reason,
            onRetry = onRetry,
            modifier = Modifier.fillMaxWidth(),
            compact = true,
        )
        NextPageStatus.Idle -> Unit
    }
}

@Composable
private fun ArtistCard(artist: Artist, onClick: () -> Unit) {
    CardSurface(onClick = onClick) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ArtistTile(artist)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
                artist.genres.firstOrNull()?.let { GenreChip(it) }
            }
        }
    }
}
