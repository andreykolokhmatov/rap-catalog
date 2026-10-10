package ru.omgtu.kolokhmatov.rapcatalog.ui.detail

import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist

data class ArtistDetailState(
    val artist: Artist,
    val related: List<Artist>,
)
