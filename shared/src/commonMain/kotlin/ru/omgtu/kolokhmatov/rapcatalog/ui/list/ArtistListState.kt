package ru.omgtu.kolokhmatov.rapcatalog.ui.list

import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist

data class ArtistListState(
    val query: String = "",
    val items: List<Artist> = emptyList(),
)
