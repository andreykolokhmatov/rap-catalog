package ru.omgtu.kolokhmatov.rapcatalog.ui.detail

sealed interface ArtistDetailIntent {
    data class RelatedArtistClicked(val id: String) : ArtistDetailIntent
}
