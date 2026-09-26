package ru.omgtu.kolokhmatov.rapcatalog.list

sealed interface ArtistListIntent {
    data class CardClicked(val id: String) : ArtistListIntent
}
