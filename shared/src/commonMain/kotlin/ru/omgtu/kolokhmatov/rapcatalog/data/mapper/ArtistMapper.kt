package ru.omgtu.kolokhmatov.rapcatalog.data.mapper

import ru.omgtu.kolokhmatov.rapcatalog.data.dto.ArtistDto
import ru.omgtu.kolokhmatov.rapcatalog.domain.Alias
import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistType
import ru.omgtu.kolokhmatov.rapcatalog.domain.Genre
import ru.omgtu.kolokhmatov.rapcatalog.domain.ReleaseGroup
import ru.omgtu.kolokhmatov.rapcatalog.domain.Tag

private const val MAX_TAGS = 5
private const val MAX_ALIASES = 6
private const val MAX_RELATED = 8

// Какие связи MusicBrainz показываем в «Связанных».
private val RELATED_TYPES = setOf("member of band", "subgroup", "collaboration", "is person")

// Q-идентификатор Викиданных из ссылок исполнителя, если есть.
internal val ArtistDto.wikidataId: String?
    get() = relations
        .firstOrNull { it.type == "wikidata" && it.url != null }
        ?.url?.resource?.substringAfterLast('/')

// Все MBID карточки — сам исполнитель и связанные: фото для них одним запросом.
internal val ArtistDto.photoIds: List<String>
    get() = listOf(id) + relations.mapNotNull { it.artist?.id }

// photos — MBID → адрес фото, для исполнителя и связанных артистов.
internal fun ArtistDto.toArtist(
    biography: String = "",
    biographyRu: String = "",
    photos: Map<String, String> = emptyMap(),
): Artist {
    val tagsByCount = tags.filter { it.count > 0 }.sortedByDescending { it.count }
    return Artist(
        id = id,
        name = name,
        sortName = sortName ?: name,
        disambiguation = disambiguation.orEmpty(),
        type = when (type) {
            "Person" -> ArtistType.PERSON
            "Group" -> ArtistType.GROUP
            else -> ArtistType.OTHER
        },
        country = country.orEmpty(),
        area = area?.name.orEmpty(),
        beginArea = beginArea?.name.orEmpty(),
        lifeSpanBegin = lifeSpan?.begin.orEmpty(),
        lifeSpanEnd = lifeSpan?.end.orEmpty(),
        ended = lifeSpan?.ended ?: false,
        // Теги MusicBrainz → жанры каталога, по убыванию голосов.
        genres = tagsByCount.mapNotNull { genreOf(it.name) }.distinct(),
        tags = tagsByCount.take(MAX_TAGS).map { Tag(it.name, it.count) },
        aliases = aliases(),
        releaseGroups = releaseGroups
            // Только номерные альбомы: без сборников, микстейпов и концертников.
            .filter { it.primaryType == "Album" && it.secondaryTypes.isEmpty() }
            .map {
                ReleaseGroup(
                    id = it.id,
                    title = it.title,
                    firstReleaseDate = it.firstReleaseDate.orEmpty(),
                    primaryType = it.primaryType.orEmpty(),
                )
            },
        related = related(photos),
        biography = biography,
        biographyRu = biographyRu,
        photoUrl = photos[id].orEmpty(),
    )
}

private fun genreOf(tag: String): Genre? {
    val name = tag.lowercase()
    return if (name == "hip-hop") Genre.HIP_HOP else Genre.fromApi(name)
}

private fun ArtistDto.aliases(): List<Alias> {
    val seen = mutableSetOf(name.lowercase())
    return aliases
        // Search hint — опечатки и транслитерации для поиска, не псевдонимы.
        .filter { it.type != "Search hint" && seen.add(it.name.lowercase()) }
        .take(MAX_ALIASES)
        .map { Alias(name = it.name, primary = it.primary ?: false, locale = it.locale.orEmpty()) }
}

private fun ArtistDto.related(photos: Map<String, String>): List<Artist> {
    val seen = mutableSetOf(id)
    return relations
        .filter { it.type in RELATED_TYPES }
        .mapNotNull { it.artist }
        .filter { seen.add(it.id) }
        .take(MAX_RELATED)
        .map { it.toArtist(photos = photos) }
}
