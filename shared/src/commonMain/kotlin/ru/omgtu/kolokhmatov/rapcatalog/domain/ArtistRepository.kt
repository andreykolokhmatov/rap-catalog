package ru.omgtu.kolokhmatov.rapcatalog.domain

interface ArtistRepository {
    fun all(): List<Artist>

    fun byId(id: String): Artist?

    fun related(artist: Artist): List<Artist>
}
