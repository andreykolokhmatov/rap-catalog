package band.effective.education.rapcatalog.list

sealed interface ArtistListIntent {
    data class CardClicked(val id: String) : ArtistListIntent
}
