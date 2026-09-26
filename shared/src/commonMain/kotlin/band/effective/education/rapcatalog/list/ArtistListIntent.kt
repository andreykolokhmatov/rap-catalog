package band.effective.education.rapcatalog.list

/**
 * Намерения — всё, что пользователь может сделать с экраном списка.
 *
 * Экран не меняет состояние сам, он только сообщает о намерении. Менять — работа
 * [ArtistListViewModel]. Отсюда однонаправленность: вниз состояние, вверх намерения,
 * и никаких путей в обход.
 */
sealed interface ArtistListIntent {
    data class CardClicked(val id: String) : ArtistListIntent
}
