package ru.omgtu.kolokhmatov.rapcatalog.detail

sealed interface ArtistDetailIntent {
    data class RelatedArtistClicked(val id: String) : ArtistDetailIntent
}
