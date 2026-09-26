package band.effective.education.rapcatalog.data

import band.effective.education.rapcatalog.domain.Artist
import band.effective.education.rapcatalog.domain.ArtistRepository

/**
 * Реализация каталога на моках.
 *
 * Единственный файл, который в В2 поменяется целиком: моки уедут, на их место
 * встанет клиент MusicBrainz. Если при этом придётся править что-то ещё — экраны
 * или ViewModel, — значит слои разложены неверно.
 */
class ArtistRepositoryImpl : ArtistRepository {

    /** Индекс по MBID: экран детали ходит по нему, а не перебирает список. */
    private val byId: Map<String, Artist> = mockArtists.associateBy { it.id }

    override fun all(): List<Artist> = mockArtists

    override fun byId(id: String): Artist? = byId[id]

    override fun related(artist: Artist): List<Artist> =
        artist.relatedArtistIds.mapNotNull { byId[it] }
}
