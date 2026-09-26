package ru.omgtu.kolokhmatov.rapcatalog.ui.components

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
import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.ui.artistPhoto
import ru.omgtu.kolokhmatov.rapcatalog.ui.genreColor
import org.jetbrains.compose.resources.painterResource

@Composable
fun ArtistAvatar(artist: Artist, size: Dp, modifier: Modifier = Modifier) {
    Artwork(
        artist = artist,
        cornerRadius = size / 4,
        letterSize = size.value * 0.42f,
        modifier = modifier.size(size),
    )
}

@Composable
fun ArtistTile(artist: Artist, modifier: Modifier = Modifier) {
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

            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center,
    ) {
        if (photo != null) {
            Image(
                painter = painterResource(photo),

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
