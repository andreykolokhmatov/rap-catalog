package ru.omgtu.kolokhmatov.rapcatalog.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import ru.omgtu.kolokhmatov.rapcatalog.data.dto.ArtistDto
import ru.omgtu.kolokhmatov.rapcatalog.data.dto.ArtistSearchDto
import kotlin.time.Duration.Companion.milliseconds

// MusicBrainz пускает не чаще запроса в секунду, иначе отвечает 503.
private val MUSICBRAINZ_INTERVAL = 1100.milliseconds

// Метод на адрес, наружу — DTO или LoadException.
internal class MusicBrainzApi(private val client: HttpClient) {
    private val throttle = RequestThrottle(MUSICBRAINZ_INTERVAL)

    // GET artist?query= — поиск на языке запросов Lucene.
    suspend fun searchArtists(query: String, limit: Int, offset: Int): ArtistSearchDto =
        loadOrThrow {
            throttle.run {
                client.get(ApiConfig.MUSICBRAINZ_URL) {
                    url.appendPathSegments("artist")
                    parameter("query", query)
                    parameter("limit", limit)
                    parameter("offset", offset)
                    parameter("fmt", "json")
                }.body()
            }
        }

    // GET artist/{id} — карточка со всем, что нужно детали, одним запросом.
    // На id не в формате MBID MusicBrainz отвечает 400 — это тоже «нет записи».
    suspend fun artist(id: String): ArtistDto = loadOrThrow(notFound = setOf(400, 404)) {
        throttle.run {
            client.get {
                url {
                    takeFrom(ApiConfig.MUSICBRAINZ_URL)
                    appendPathSegments("artist", id)
                }
                parameter("inc", "aliases+tags+artist-rels+url-rels+release-groups")
                // Фильтр для release-groups: только альбомы.
                parameter("type", "album")
                parameter("fmt", "json")
            }.body()
        }
    }
}
