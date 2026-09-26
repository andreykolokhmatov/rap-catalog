package band.effective.education.rapcatalog.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import band.effective.education.rapcatalog.domain.Artist
import band.effective.education.rapcatalog.list.ArtistListIntent
import band.effective.education.rapcatalog.list.ArtistListState
import band.effective.education.rapcatalog.resources.Res
import band.effective.education.rapcatalog.resources.list_count
import band.effective.education.rapcatalog.ui.components.ArtistTile
import band.effective.education.rapcatalog.ui.components.CardSurface
import band.effective.education.rapcatalog.ui.components.GenreChip
import org.jetbrains.compose.resources.stringResource

/** Колонок в сетке. Число фиксированное: в этой вехе один макет, адаптив — В3. */
private const val COLUMNS = 3

/** Предел ширины колонки контента: дальше растут поля, а не плитки. */
private val MAX_CONTENT_WIDTH = 1040.dp

/**
 * Экран списка — сетка карточек.
 *
 * Строками во всю ширину каталог читался плохо: на широком окне у каждой записи
 * оставалось полтора метра пустоты справа, а на экран помещалось семь имён.
 * Плитка с крупной обложкой, именем и жанром даёт втрое больше записей на экран
 * и сравнимую с обложками сетку, к которой музыкальные каталоги и приучают.
 *
 * Вниз уходят состояние и колбэк, а не ViewModel целиком: отданная целиком
 * ViewModel выводится компилятором как `Unstable` и убивает пропуск рекомпозиции.
 * С такой сигнатурой экран остаётся `restartable skippable` — это видно в отчёте
 * компилятора, в каталоге `shared/build/compose_compiler`.
 */
@Composable
fun ArtistListScreen(
    state: ArtistListState,
    onIntent: (ArtistListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        // Колонка контента упирается в предел по ширине и дальше не растёт.
        // Три колонки на весь экран дают плитки в пол-окна: на широком мониторе
        // помещается полтора ряда, и каталог снова приходится листать по одной
        // записи. Это не адаптив: макет один, меняется только поле по краям.
        modifier = Modifier.widthIn(max = MAX_CONTENT_WIDTH),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(COLUMNS) }) {
            Text(
                text = stringResource(Res.string.list_count, state.items.size).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        // Ключ по MBID обязателен: без него ячейки перетасовываются при любом
        // изменении набора. Сетка ленивая — на экране живут только видимые плитки.
        items(items = state.items, key = { it.id }) { artist ->
            ArtistCard(
                artist = artist,
                onClick = { onIntent(ArtistListIntent.CardClicked(artist.id)) },
            )
        }
    }
    }
}

/**
 * Одна карточка сетки: обложка, имя, ведущий жанр.
 *
 * Жанр ровно один и в одну строку: компактная метка, переехавшая на вторую
 * строку, ломает ритм сетки, а обрезанная посередине перестаёт читаться.
 * Полный список жанров ждёт на экране детали.
 */
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
