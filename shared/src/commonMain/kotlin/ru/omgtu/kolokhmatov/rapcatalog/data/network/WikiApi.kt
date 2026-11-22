package ru.omgtu.kolokhmatov.rapcatalog.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.encodeURLParameter
import ru.omgtu.kolokhmatov.rapcatalog.data.dto.WikiSummaryDto
import ru.omgtu.kolokhmatov.rapcatalog.data.dto.WikidataEntitiesDto
import ru.omgtu.kolokhmatov.rapcatalog.data.dto.WikidataImagesDto

// Фото не должны задерживать список: не успели — список покажется с буквами.
private const val SPARQL_TIMEOUT_MS = 5_000L

// Справка и фото: Викиданные дают названия статей и фото с Викисклада,
// Википедия — первый абзац. Лимита как у MusicBrainz здесь нет.
internal class WikiApi(private val client: HttpClient) {

    // GET wikidata.org/w/api.php?action=wbgetentities — статьи о сущности.
    suspend fun sitelinks(wikidataId: String, wikis: List<String>): WikidataEntitiesDto =
        loadOrThrow {
            client.get(ApiConfig.WIKIDATA_URL) {
                parameter("action", "wbgetentities")
                parameter("ids", wikidataId)
                parameter("props", "sitelinks")
                parameter("sitefilter", wikis.joinToString("|"))
                parameter("format", "json")
                // Без этого Викиданные не отдают заголовок CORS.
                parameter("origin", "*")
            }.body()
        }

    // GET query.wikidata.org/sparql — фото (P18) исполнителей по их MBID (P434).
    suspend fun images(mbids: List<String>): WikidataImagesDto = loadOrThrow {
        val values = mbids.joinToString(" ") { "\"$it\"" }
        client.get(ApiConfig.WIKIDATA_SPARQL_URL) {
            parameter(
                "query",
                "SELECT ?mbid ?image WHERE { VALUES ?mbid { $values } " +
                    "?item wdt:P434 ?mbid; wdt:P18 ?image. }",
            )
            parameter("format", "json")
            timeout { requestTimeoutMillis = SPARQL_TIMEOUT_MS }
        }.body()
    }

    // GET {lang}.wikipedia.org/api/rest_v1/page/summary/{title}
    suspend fun summary(lang: String, title: String): WikiSummaryDto = loadOrThrow {
        val path = title.replace(' ', '_').encodeURLParameter()
        client.get(ApiConfig.WIKIPEDIA_SUMMARY_URL.replace("{lang}", lang) + path).body()
    }
}
