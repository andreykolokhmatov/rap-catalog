package band.effective.education.rapcatalog.detail

sealed interface ArtistDetailIntent {
    data class RelatedArtistClicked(val id: String) : ArtistDetailIntent
}
