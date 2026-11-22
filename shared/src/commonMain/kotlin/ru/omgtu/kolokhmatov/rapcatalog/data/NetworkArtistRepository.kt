package ru.omgtu.kolokhmatov.rapcatalog.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.omgtu.kolokhmatov.rapcatalog.data.mapper.photoIds
import ru.omgtu.kolokhmatov.rapcatalog.data.mapper.toArtist
import ru.omgtu.kolokhmatov.rapcatalog.data.mapper.toPhotoUrls
import ru.omgtu.kolokhmatov.rapcatalog.data.mapper.wikidataId
import ru.omgtu.kolokhmatov.rapcatalog.data.network.MusicBrainzApi
import ru.omgtu.kolokhmatov.rapcatalog.data.network.WikiApi
import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistPage
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository
import ru.omgtu.kolokhmatov.rapcatalog.domain.LoadException

// Каталог — исполнители и группы с тегами «hip hop» и «rap».
private const val CATALOG_QUERY = """tag:"hip hop" AND tag:rap AND (type:person OR type:group)"""

private val LUCENE_SPECIAL = Regex("""[+\-&|!(){}\[\]^"~*?:\\/]""")
private val WHITESPACE = Regex("""\s+""")

private const val LANG_EN = "en"
private const val LANG_RU = "ru"

// Все запросы и разбор ответов идут на dispatcher, а не на главном потоке:
// JSON детали со связями и дискографией — десятки килобайт.
internal class NetworkArtistRepository(
    private val musicBrainz: MusicBrainzApi,
    private val wiki: WikiApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ArtistRepository {

    // Кэш карточек в памяти сессии: «назад» и повторный переход на артиста
    // не ждут лимита MusicBrainz. На диск ничего не пишется.
    private val details = mutableMapOf<String, Artist>()
    private val detailsLock = Mutex()

    override suspend fun artists(query: String, offset: Int): ArtistPage = withContext(dispatcher) {
        val page = musicBrainz.searchArtists(
            query = luceneQuery(query),
            limit = ArtistRepository.PAGE_SIZE,
            offset = offset,
        )
        val photos = photos(page.artists.map { it.id }).orEmpty()
        ArtistPage(artists = page.artists.map { it.toArtist(photos = photos) }, total = page.count)
    }

    override suspend fun artist(id: String): Artist? = withContext(dispatcher) {
        detailsLock.withLock { details[id] }?.let { return@withContext it }

        val dto = try {
            musicBrainz.artist(id)
        } catch (_: LoadException.NotFound) {
            return@withContext null
        }
        // Справка и фото с разных серверов: оба запроса уходят сразу, ждём оба.
        // Если экран закроют, отменятся оба вместе с родителем.
        val (artist, complete) = coroutineScope {
            val biographies = async { biographies(dto.wikidataId) }
            val photos = async { photos(dto.photoIds) }
            val bio = biographies.await()
            val photoUrls = photos.await()
            val artist = dto.toArtist(bio.en, bio.ru, photoUrls ?: emptyMap())
            artist to (bio.complete && photoUrls != null)
        }
        // Справка или фото не загрузились — в кэш не кладём: при следующем
        // открытии карточки дополнения запросятся снова.
        if (complete) detailsLock.withLock { details[id] = artist }
        artist
    }

    // Фото — дополнение с другого сервера: без него рисуется буквенная плитка,
    // поэтому его сбой не валит список. Отмена при этом проходит наверх:
    // ловится только LoadException. null — фото не загрузились.
    private suspend fun photos(ids: List<String>): Map<String, String>? {
        if (ids.isEmpty()) return emptyMap()
        return try {
            wiki.images(ids).toPhotoUrls()
        } catch (_: LoadException) {
            null
        }
    }

    private class Biographies(val en: String, val ru: String, val complete: Boolean)

    // Первые абзацы статей английской и русской Википедии, грузятся параллельно.
    // Справка тоже дополнение: её сбой экран не роняет, сбой MusicBrainz — роняет.
    private suspend fun biographies(wikidataId: String?): Biographies {
        if (wikidataId == null) return Biographies("", "", complete = true)
        val sitelinks = try {
            wiki.sitelinks(wikidataId, listOf("${LANG_EN}wiki", "${LANG_RU}wiki"))
                .entities[wikidataId]?.sitelinks.orEmpty()
        } catch (_: LoadException) {
            return Biographies("", "", complete = false)
        }
        return coroutineScope {
            val en = async { extract(LANG_EN, sitelinks["${LANG_EN}wiki"]?.title) }
            val ru = async { extract(LANG_RU, sitelinks["${LANG_RU}wiki"]?.title) }
            val (enText, enOk) = en.await()
            val (ruText, ruOk) = ru.await()
            Biographies(enText.orEmpty(), ruText.orEmpty(), complete = enOk && ruOk)
        }
    }

    // Текст и признак, что запрос не упал. Нет статьи — это не сбой.
    private suspend fun extract(lang: String, title: String?): Pair<String?, Boolean> {
        if (title == null) return null to true
        return try {
            wiki.summary(lang, title).extract to true
        } catch (_: LoadException) {
            null to false
        }
    }
}

// «lil way» → «… AND artist:(lil* AND way*)»: каждое слово — начало слова в имени.
internal fun luceneQuery(name: String): String {
    val words = name.trim()
        .split(WHITESPACE)
        .filter { it.isNotEmpty() }
        .map { word -> word.replace(LUCENE_SPECIAL) { "\\" + it.value } + "*" }
    if (words.isEmpty()) return CATALOG_QUERY
    return "$CATALOG_QUERY AND artist:(${words.joinToString(" AND ")})"
}
