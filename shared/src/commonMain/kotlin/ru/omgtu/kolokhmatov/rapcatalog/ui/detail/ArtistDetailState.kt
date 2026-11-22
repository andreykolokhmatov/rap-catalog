package ru.omgtu.kolokhmatov.rapcatalog.ui.detail

import androidx.compose.runtime.Immutable
import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.ui.ErrorReason

@Immutable
sealed interface ArtistDetailState {
    data object Loading : ArtistDetailState

    data object NotFound : ArtistDetailState

    data class Error(val reason: ErrorReason) : ArtistDetailState

    // Связанные артисты — в artist.related, они приходят тем же запросом.
    data class Loaded(val artist: Artist) : ArtistDetailState
}
