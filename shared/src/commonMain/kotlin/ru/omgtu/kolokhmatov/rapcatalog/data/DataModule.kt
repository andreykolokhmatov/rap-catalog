package ru.omgtu.kolokhmatov.rapcatalog.data

import io.ktor.client.HttpClient
import ru.omgtu.kolokhmatov.rapcatalog.data.network.MusicBrainzApi
import ru.omgtu.kolokhmatov.rapcatalog.data.network.WikiApi
import ru.omgtu.kolokhmatov.rapcatalog.data.network.createHttpClient
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository

// Сборка data-слоя: один HttpClient на приложение, наружу — интерфейс.
class DataModule {
    val httpClient: HttpClient = createHttpClient()

    val artistRepository: ArtistRepository = NetworkArtistRepository(
        musicBrainz = MusicBrainzApi(httpClient),
        wiki = WikiApi(httpClient),
    )
}
