package ru.omgtu.kolokhmatov.rapcatalog.ui.list

sealed interface ArtistListIntent {
    data class CardClicked(val id: String) : ArtistListIntent
    data class QueryChanged(val query: String) : ArtistListIntent
}
