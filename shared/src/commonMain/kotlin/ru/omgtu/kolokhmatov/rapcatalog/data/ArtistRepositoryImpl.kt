package ru.omgtu.kolokhmatov.rapcatalog.data

import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository

class ArtistRepositoryImpl : ArtistRepository {
    private val byId: Map<String, Artist> = mockArtists.associateBy { it.id }

    override fun all(): List<Artist> = mockArtists

    override fun byId(id: String): Artist? = byId[id]

    override fun related(artist: Artist): List<Artist> =
        artist.relatedArtistIds.mapNotNull { byId[it] }
}
