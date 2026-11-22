package ru.omgtu.kolokhmatov.rapcatalog.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import ru.omgtu.kolokhmatov.rapcatalog.data.mapper.toArtist
import ru.omgtu.kolokhmatov.rapcatalog.data.network.MusicBrainzApi
import ru.omgtu.kolokhmatov.rapcatalog.data.network.configureApi
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistType
import ru.omgtu.kolokhmatov.rapcatalog.domain.Genre
import ru.omgtu.kolokhmatov.rapcatalog.domain.LoadException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class ApiTest {
    private fun api(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData) =
        MusicBrainzApi(HttpClient(MockEngine(handler)) { configureApi() })

    private fun MockRequestHandleScope.json(body: String, status: HttpStatusCode = HttpStatusCode.OK) =
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))

    @Test
    fun unknownFieldsAreIgnored() = runTest {
        val page = api {
            json(
                """
                {"created":"2026-11-22T00:00:00Z","count":1,"offset":0,"artists":[{
                  "id":"b95ce3ff-3d05-4e87-9e01-c97b66af13d4","name":"Eminem","type":"Person",
                  "score":100,"new-field-from-next-release":{"x":1},
                  "life-span":{"begin":"1972-10-17","ended":null},
                  "tags":[{"count":38,"name":"hip hop"},{"count":-1,"name":"pop"}]
                }]}
                """.trimIndent(),
            )
        }.searchArtists(query = "", limit = 30, offset = 0)

        val artist = page.artists.single().toArtist()
        assertEquals("Eminem", artist.name)
        assertEquals(ArtistType.PERSON, artist.type)
        assertEquals(listOf(Genre.HIP_HOP), artist.genres)
        assertEquals(false, artist.ended)
    }

    @Test
    fun notFoundAndBadIdBecomeNotFound() = runTest {
        assertFailsWith<LoadException.NotFound> {
            api { json("{}", HttpStatusCode.NotFound) }.artist("missing")
        }
        assertFailsWith<LoadException.NotFound> {
            api { json("{}", HttpStatusCode.BadRequest) }.artist("not-a-mbid")
        }
    }

    @Test
    fun serverErrorKeepsStatus() = runTest {
        val error = assertFailsWith<LoadException.Server> {
            api { json("{}", HttpStatusCode.InternalServerError) }.searchArtists("", 30, 0)
        }
        assertEquals(500, error.status)
    }

    @Test
    fun brokenBodyIsBadResponse() = runTest {
        assertFailsWith<LoadException.BadResponse> {
            api { json("""{"count":"many"}""") }.searchArtists("", 30, 0)
        }
        assertFailsWith<LoadException.BadResponse> {
            api { json("<html>proxy error</html>") }.searchArtists("", 30, 0)
        }
    }

    @Test
    fun retriesOnceOn503() = runTest {
        var calls = 0
        val page = api {
            calls++
            if (calls == 1) json("{}", HttpStatusCode.ServiceUnavailable) else json("""{"count":0}""")
        }.searchArtists("", 30, 0)
        assertEquals(2, calls)
        assertEquals(0, page.count)
    }

    // Отмену loadOrThrow не превращает в «нет связи»: наверх уходит она сама.
    @Test
    fun cancellationIsNotLoadException() = runTest {
        val slow = api { awaitCancellation() }
        assertFailsWith<TimeoutCancellationException> {
            withTimeout(100) { slow.searchArtists("", 30, 0) }
        }
    }

    @Test
    fun luceneQueryEscapesAndPrefixesWords() {
        assertEquals(
            """tag:"hip hop" AND tag:rap AND (type:person OR type:group) AND artist:(lil* AND way*)""",
            luceneQuery("  lil   way "),
        )
        assertEquals(
            """tag:"hip hop" AND tag:rap AND (type:person OR type:group) AND artist:(ac\/dc*)""",
            luceneQuery("ac/dc"),
        )
    }
}
