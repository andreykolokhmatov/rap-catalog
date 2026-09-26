package band.effective.education.rapcatalog.list

import band.effective.education.rapcatalog.domain.Artist

data class ArtistListState(
    val items: List<Artist> = emptyList(),
)
