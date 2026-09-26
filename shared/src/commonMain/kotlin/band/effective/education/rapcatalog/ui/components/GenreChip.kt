package band.effective.education.rapcatalog.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import band.effective.education.rapcatalog.domain.Genre
import band.effective.education.rapcatalog.ui.genreColor
import band.effective.education.rapcatalog.ui.genreLabel
import org.jetbrains.compose.resources.stringResource

/**
 * Бейдж жанра.
 *
 * Цвет подложки берётся из [genreColor] — единственное место, где цвет не из темы:
 * он несёт смысл данных и потому одинаков в обеих темах. Текст поверх всегда белый,
 * палитра под это проверена на контраст 4.5:1.
 *
 * Жанр подписан словом, а не только цветом: цвет один смысл передавать не должен —
 * его не увидит ни скринридер, ни человек с дальтонизмом.
 */
@Composable
fun GenreChip(genre: Genre, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = genreColor(genre),
        contentColor = Color.White,
    ) {
        Text(
            text = stringResource(genreLabel(genre)),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
