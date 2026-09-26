package band.effective.education.rapcatalog.domain

/**
 * Откуда экраны берут артистов.
 *
 * Интерфейс живёт в domain, реализация — в data. В В1 реализация отдаёт моки,
 * в В2 на её место встанет сеть, и ни один экран об этом не узнает.
 */
interface ArtistRepository {
    /** Весь каталог. В В2 здесь появятся limit/offset. */
    fun all(): List<Artist>

    /** Одна запись по MBID. `null` — записи нет: экран обязан это пережить. */
    fun byId(id: String): Artist?

    /** Связанные артисты, уже раскрытые в записи. Порядок — как в [Artist.relatedArtistIds]. */
    fun related(artist: Artist): List<Artist>
}
