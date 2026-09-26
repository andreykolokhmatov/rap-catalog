package band.effective.education.rapcatalog.domain

/**
 * Жанр артиста.
 *
 * В MusicBrainz жанр приходит строкой (`genres[].name`, `tags[].name`), поэтому
 * [apiName] хранится рядом с константой: в В2 сериализация разбирает строку через
 * [fromApi], а весь остальной код работает с типом, в котором нельзя опечататься.
 *
 * Список закрытый и покрывает набор из [band.effective.education.rapcatalog.data.mockArtists].
 * Жанр, которого здесь нет, каталог просто не показывает — это осознанный выбор:
 * тегов в MusicBrainz тысячи, и рисовать их все нечем.
 */
enum class Genre(val apiName: String) {
    HIP_HOP("hip hop"),
    RAP("rap"),
    POP_RAP("pop rap"),
    EAST_COAST("east coast hip hop"),
    WEST_COAST("west coast hip hop"),
    SOUTHERN("southern hip hop"),
    GANGSTA_RAP("gangsta rap"),
    G_FUNK("g-funk"),
    CONSCIOUS("conscious hip hop"),
    POLITICAL("political hip hop"),
    HARDCORE("hardcore hip hop"),
    BOOM_BAP("boom bap"),
    JAZZ_RAP("jazz rap"),
    INSTRUMENTAL("instrumental hip hop"),
    EXPERIMENTAL("experimental hip hop"),
    ALTERNATIVE("alternative hip hop"),
    UNDERGROUND("underground hip hop"),
    OLD_SCHOOL("old school hip hop"),
    RAP_ROCK("rap rock"),
    TRAP("trap"),
    CRUNK("crunk"),
    EMO_RAP("emo rap"),
    CLOUD_RAP("cloud rap"),
    TRAP_METAL("trap metal"),
    RAGE("rage"),
    ;

    companion object {
        /** Разбор строки из API. Неизвестный жанр отбрасывается, а не роняет запись. */
        fun fromApi(name: String): Genre? = entries.firstOrNull { it.apiName == name }
    }
}
