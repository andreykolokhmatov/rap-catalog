package band.effective.education.rapcatalog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.education.rapcatalog.domain.Artist
import band.effective.education.rapcatalog.ui.artistPhoto
import band.effective.education.rapcatalog.ui.genreColor
import org.jetbrains.compose.resources.painterResource

/**
 * Аватар артиста — первая буква имени на плитке цвета ведущего жанра.
 *
 * Картинок в этой вехе нет намеренно: MusicBrainz фотографий не отдаёт, обложки
 * живут в Cover Art Archive и приедут в В2. Буква на цветной плитке различает
 * записи в списке не хуже и не требует ни одного байта из сети.
 *
 * Два жанра дают диагональную заливку из двух цветов — так соседние карточки
 * отличаются друг от друга даже при одном ведущем жанре.
 */
@Composable
fun ArtistAvatar(artist: Artist, size: Dp, modifier: Modifier = Modifier) {
    Artwork(
        artist = artist,
        cornerRadius = size / 4,
        letterSize = size.value * 0.42f,
        modifier = modifier.size(size),
    )
}

/**
 * Та же заливка, но во всю ширину ячейки и квадратом.
 *
 * Нужна сетке: там плитка занимает всю ширину колонки, а её высота задаётся
 * соотношением сторон, а не числом в dp — иначе при трёх колонках плитки
 * перестают быть квадратными.
 */
@Composable
fun ArtistTile(artist: Artist, modifier: Modifier = Modifier) {
    // Кегль буквы считается от ширины ячейки, а не задан числом: колонка сетки
    // растягивается вместе с окном, и фиксированный кегль на широком экране
    // превращался бы в точку посреди плитки.
    BoxWithConstraints(modifier = modifier.fillMaxWidth().aspectRatio(1f)) {
        Artwork(
            artist = artist,
            cornerRadius = 12.dp,
            letterSize = maxWidth.value * 0.4f,
            modifier = Modifier.matchParentSize(),
        )
    }
}

@Composable
private fun Artwork(
    artist: Artist,
    cornerRadius: Dp,
    letterSize: Float,
    modifier: Modifier,
) {
    val photo = artistPhoto(artist)
    val fallback = MaterialTheme.colorScheme.primary
    val colors = when {
        artist.genres.isEmpty() -> listOf(fallback, fallback)
        artist.genres.size == 1 -> {
            val single = genreColor(artist.genres.first())
            listOf(single, single)
        }
        else -> listOf(genreColor(artist.genres[0]), genreColor(artist.genres[1]))
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            // Градиент остаётся подложкой и под фотографией: пока картинка
            // декодируется, на её месте цветной прямоугольник, а не дыра.
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center,
    ) {
        if (photo != null) {
            Image(
                painter = painterResource(photo),
                // Снимок несёт смысл, а не украшает, поэтому подпись обязательна.
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        } else {
            Text(
                text = artist.name.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = letterSize.sp,
            )
        }
    }
}
