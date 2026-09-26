package band.effective.education.rapcatalog.detail

import band.effective.education.rapcatalog.domain.Artist

data class ArtistDetailState(
    val artist: Artist,
    val related: List<Artist>,
)
