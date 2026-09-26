package ru.omgtu.kolokhmatov.rapcatalog.list

import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist

data class ArtistListState(
    val items: List<Artist> = emptyList(),
)
