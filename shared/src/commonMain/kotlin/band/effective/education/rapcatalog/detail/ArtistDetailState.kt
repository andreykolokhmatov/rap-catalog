package band.effective.education.rapcatalog.detail

import band.effective.education.rapcatalog.domain.Artist

/**
 * Состояние экрана детали — всё, что на нём видно, одним объектом.
 *
 * [related] уже раскрыты в записи: экран не ходит в репозиторий за именами
 * связанных артистов, он рисует то, что ему дали.
 */
data class ArtistDetailState(
    val artist: Artist,
    val related: List<Artist>,
)
