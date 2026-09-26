package band.effective.education.rapcatalog.data

import band.effective.education.rapcatalog.domain.Artist
import band.effective.education.rapcatalog.domain.ArtistRepository

class ArtistRepositoryImpl : ArtistRepository {
    private val byId: Map<String, Artist> = mockArtists.associateBy { it.id }

    override fun all(): List<Artist> = mockArtists

    override fun byId(id: String): Artist? = byId[id]

    override fun related(artist: Artist): List<Artist> =
        artist.relatedArtistIds.mapNotNull { byId[it] }
}
