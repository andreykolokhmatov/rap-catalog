package band.effective.education.rapcatalog.list

import band.effective.education.rapcatalog.domain.Artist

/**
 * Состояние экрана списка — **всё**, что нужно нарисовать, одним объектом.
 *
 * Экран ни у кого не спрашивает «а как там дела»: он получает состояние и рисует его.
 * Если что-то на экране видно, а в этом классе этого нет — значит нарисовано мимо
 * архитектуры.
 */
data class ArtistListState(
    val items: List<Artist> = emptyList(),
)
