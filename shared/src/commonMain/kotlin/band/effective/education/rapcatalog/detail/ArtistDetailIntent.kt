package band.effective.education.rapcatalog.detail

/**
 * Намерения экрана детали.
 *
 * Связанный артист — такой же переход вперёд, как и клик по карточке в списке:
 * стек растёт, «назад» возвращает туда, откуда пришли.
 */
sealed interface ArtistDetailIntent {
    data class RelatedArtistClicked(val id: String) : ArtistDetailIntent
}
