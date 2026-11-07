package ru.omgtu.kolokhmatov.rapcatalog.ui.components

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
import ru.omgtu.kolokhmatov.rapcatalog.domain.Genre
import ru.omgtu.kolokhmatov.rapcatalog.ui.genreColor
import ru.omgtu.kolokhmatov.rapcatalog.ui.genreLabel
import org.jetbrains.compose.resources.stringResource

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
