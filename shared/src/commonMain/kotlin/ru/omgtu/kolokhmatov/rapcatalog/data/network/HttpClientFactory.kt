package ru.omgtu.kolokhmatov.rapcatalog.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val REQUEST_TIMEOUT_MS = 15_000L
private const val RETRY_DELAY_MS = 2_000L

// Чужой API меняется без нас: незнакомые поля пропускаются, null в поле
// со значением по умолчанию превращается в это значение.
internal val ApiJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

internal fun createHttpClient(): HttpClient = HttpClient(httpEngine) { configureApi() }

// Отдельно от движка: в тестах та же настройка ставится на MockEngine.
internal fun HttpClientConfig<*>.configureApi() {
    // Ответ 4xx/5xx — исключение, его переводит в LoadException loadOrThrow.
    expectSuccess = true

    install(ContentNegotiation) {
        json(ApiJson)
        // SPARQL Викиданных отвечает своим типом, хотя внутри тот же JSON.
        json(ApiJson, ContentType.parse("application/sparql-results+json"))
    }
    install(HttpTimeout) {
        // Только общий таймаут: таймаутов соединения и сокета у fetch в браузере нет.
        requestTimeoutMillis = REQUEST_TIMEOUT_MS
    }
    install(HttpRequestRetry) {
        // 503 у MusicBrainz — «слишком часто». Один повтор через паузу,
        // второй 503 уходит наверх. Сбои связи сами не повторяются.
        val musicBrainz = Url(ApiConfig.MUSICBRAINZ_URL).host
        retryOnExceptionIf(maxRetries = 1) { _, _ -> false }
        retryIf(maxRetries = 1) { request, response ->
            response.status == HttpStatusCode.ServiceUnavailable && request.url.host == musicBrainz
        }
        constantDelay(RETRY_DELAY_MS)
    }
    if (canSetUserAgent) {
        install(UserAgent) { agent = ApiConfig.USER_AGENT }
    }
}
