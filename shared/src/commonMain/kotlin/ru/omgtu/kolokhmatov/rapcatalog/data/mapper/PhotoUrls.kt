package ru.omgtu.kolokhmatov.rapcatalog.data.mapper

import io.ktor.http.decodeURLPart
import io.ktor.http.encodeURLParameter
import okio.ByteString.Companion.encodeUtf8
import ru.omgtu.kolokhmatov.rapcatalog.data.dto.WikidataImagesDto
import ru.omgtu.kolokhmatov.rapcatalog.data.network.ApiConfig

private const val THUMB_WIDTH = 330
private const val FILE_PATH_MARKER = "Special:FilePath/"

// MBID → адрес миниатюры. У исполнителя может быть несколько фото, берётся первое.
internal fun WikidataImagesDto.toPhotoUrls(): Map<String, String> {
    val urls = mutableMapOf<String, String>()
    for (row in results.bindings) {
        val url = thumbUrl(row.image.value) ?: continue
        urls.getOrPut(row.mbid.value) { url }
    }
    return urls
}

// Викиданные отдают Special:FilePath, а он отвечает редиректами без заголовка
// CORS, и в браузере картинка не загрузится. Прямой адрес миниатюры Викисклада
// строится из MD5 имени файла: thumb/a/ab/Имя/330px-Имя.
private fun thumbUrl(filePath: String): String? {
    val at = filePath.indexOf(FILE_PATH_MARKER)
    if (at < 0) return null
    val name = filePath.substring(at + FILE_PATH_MARKER.length).decodeURLPart().replace(' ', '_')
    val hash = name.encodeUtf8().md5().hex()
    val encoded = name.encodeURLParameter()
    // Миниатюра SVG и TIFF отдаётся растром.
    val suffix = when (name.substringAfterLast('.').lowercase()) {
        "svg" -> ".png"
        "tif", "tiff" -> ".jpg"
        else -> ""
    }
    return "${ApiConfig.COMMONS_THUMB_URL}${hash[0]}/${hash.take(2)}/$encoded/" +
        "${THUMB_WIDTH}px-$encoded$suffix"
}
