package band.effective.education.rapcatalog.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.education.rapcatalog.detail.ArtistDetailIntent
import band.effective.education.rapcatalog.detail.ArtistDetailState
import band.effective.education.rapcatalog.domain.Artist
import band.effective.education.rapcatalog.domain.ArtistType
import band.effective.education.rapcatalog.resources.Res
import band.effective.education.rapcatalog.resources.alias_primary
import band.effective.education.rapcatalog.resources.detail_not_found
import band.effective.education.rapcatalog.resources.label_born
import band.effective.education.rapcatalog.resources.label_country
import band.effective.education.rapcatalog.resources.label_group_range
import band.effective.education.rapcatalog.resources.label_group_since
import band.effective.education.rapcatalog.resources.label_lifespan
import band.effective.education.rapcatalog.resources.label_origin
import band.effective.education.rapcatalog.resources.section_about
import band.effective.education.rapcatalog.resources.section_aliases
import band.effective.education.rapcatalog.resources.section_related
import band.effective.education.rapcatalog.resources.section_releases
import band.effective.education.rapcatalog.resources.section_tags
import band.effective.education.rapcatalog.ui.AppLanguage
import band.effective.education.rapcatalog.ui.LocalAppLanguage
import band.effective.education.rapcatalog.ui.artistTypeLabel
import band.effective.education.rapcatalog.ui.components.ArtistAvatar
import band.effective.education.rapcatalog.ui.components.CardSurface
import band.effective.education.rapcatalog.ui.components.GenreChip
import band.effective.education.rapcatalog.ui.components.Section
import band.effective.education.rapcatalog.ui.components.TagBar
import band.effective.education.rapcatalog.ui.genreColor
import org.jetbrains.compose.resources.stringResource

private val MAX_CONTENT_WIDTH = 1040.dp

@Composable
fun ArtistDetailScreen(
    state: ArtistDetailState?,
    onIntent: (ArtistDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(Res.string.detail_not_found),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    val artist = state.artist
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Header(artist)

        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .widthIn(max = MAX_CONTENT_WIDTH)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Facts(artist)

            val biography = when {
                LocalAppLanguage.current == AppLanguage.RU &&
                    artist.biographyRu.isNotEmpty() -> artist.biographyRu
                else -> artist.biography
            }
            if (biography.isNotEmpty()) {
                Section(Res.string.section_about) {
                    Text(
                        text = biography,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,

                        modifier = Modifier.widthIn(max = 680.dp),
                    )
                }
            }

            if (artist.aliases.isNotEmpty()) {
                Section(Res.string.section_aliases) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        artist.aliases.forEach { alias ->
                            AliasPill(
                                text = if (alias.primary) {
                                    "${alias.name} · ${stringResource(Res.string.alias_primary)}"
                                } else {
                                    alias.name
                                },
                            )
                        }
                    }
                }
            }

            if (artist.releaseGroups.isNotEmpty()) {
                Section(Res.string.section_releases) {
                    Column {
                        artist.releaseGroups
                            .sortedBy { it.firstReleaseDate }
                            .forEachIndexed { index, release ->
                                if (index > 0) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    Text(
                                        text = release.releaseYear,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.width(40.dp),
                                    )
                                    Text(
                                        text = release.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                    }
                }
            }

            if (artist.tags.isNotEmpty()) {
                val maxCount = artist.tags.maxOf { it.count }
                Section(Res.string.section_tags) {
                    Column { artist.tags.forEach { TagBar(tag = it, maxCount = maxCount) } }
                }
            }

            if (state.related.isNotEmpty()) {
                Section(Res.string.section_related) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.related.forEach { related ->
                            RelatedRow(
                                artist = related,
                                onClick = {
                                    onIntent(ArtistDetailIntent.RelatedArtistClicked(related.id))
                                },
                            )
                        }
                    }
                }
            }
        }

        Box(Modifier.height(8.dp))
    }
}

@Composable
private fun Header(artist: Artist) {
    val accent = artist.genres.firstOrNull()
        ?.let(::genreColor)
        ?: MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.35f), Color.Transparent),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ArtistAvatar(artist = artist, size = 120.dp)
            Text(
                text = artist.name,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = artist.disambiguation.ifEmpty {
                    stringResource(artistTypeLabel(artist.type))
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                artist.genres.forEach { GenreChip(it) }
            }
        }
    }
}

@Composable
private fun Facts(artist: Artist) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Fact(stringResource(Res.string.label_country, artist.country))
        if (artist.beginArea.isNotEmpty()) {
            Fact(stringResource(Res.string.label_origin, artist.beginArea, artist.area))
        }

        if (artist.lifeSpanBegin.isNotEmpty()) {
            val isGroup = artist.type == ArtistType.GROUP
            Fact(
                when {
                    isGroup && artist.ended -> stringResource(
                        Res.string.label_group_range,
                        artist.lifeSpanBegin,
                        artist.lifeSpanEnd,
                    )
                    isGroup -> stringResource(Res.string.label_group_since, artist.lifeSpanBegin)
                    artist.ended -> stringResource(
                        Res.string.label_lifespan,
                        artist.lifeSpanBegin,
                        artist.lifeSpanEnd,
                    )
                    else -> stringResource(Res.string.label_born, artist.lifeSpanBegin)
                },
            )
        }
    }
}

@Composable
private fun AliasPill(text: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun Fact(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun RelatedRow(artist: Artist, onClick: () -> Unit) {
    CardSurface(onClick = onClick) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArtistAvatar(artist = artist, size = 40.dp)
            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "›",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
