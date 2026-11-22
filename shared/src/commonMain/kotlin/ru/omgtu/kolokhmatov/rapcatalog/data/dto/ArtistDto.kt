package ru.omgtu.kolokhmatov.rapcatalog.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Модели ответа MusicBrainz. Это не модели экрана: в Artist их переводит
// ArtistMapper. Ключи API — через дефис, поэтому @SerialName.

@Serializable
internal data class ArtistSearchDto(
    val count: Int = 0,
    val offset: Int = 0,
    val artists: List<ArtistDto> = emptyList(),
)

@Serializable
internal data class ArtistDto(
    val id: String,
    val name: String,
    @SerialName("sort-name") val sortName: String? = null,
    val disambiguation: String? = null,
    val type: String? = null,
    val country: String? = null,
    val area: AreaDto? = null,
    @SerialName("begin-area") val beginArea: AreaDto? = null,
    @SerialName("life-span") val lifeSpan: LifeSpanDto? = null,
    val tags: List<TagDto> = emptyList(),
    val aliases: List<AliasDto> = emptyList(),
    @SerialName("release-groups") val releaseGroups: List<ReleaseGroupDto> = emptyList(),
    val relations: List<RelationDto> = emptyList(),
)

@Serializable
internal data class AreaDto(val name: String = "")

@Serializable
internal data class LifeSpanDto(
    val begin: String? = null,
    val end: String? = null,
    val ended: Boolean? = null,
)

@Serializable
internal data class TagDto(val name: String, val count: Int = 0)

@Serializable
internal data class AliasDto(
    val name: String,
    val type: String? = null,
    val primary: Boolean? = null,
    // Язык, для которого псевдоним основной: en, ja, en_US.
    val locale: String? = null,
)

@Serializable
internal data class ReleaseGroupDto(
    val id: String,
    val title: String,
    @SerialName("first-release-date") val firstReleaseDate: String? = null,
    @SerialName("primary-type") val primaryType: String? = null,
    @SerialName("secondary-types") val secondaryTypes: List<String> = emptyList(),
)

// Связь с другим исполнителем (artist) или со ссылкой (url).
@Serializable
internal data class RelationDto(
    val type: String,
    @SerialName("target-type") val targetType: String? = null,
    val artist: ArtistDto? = null,
    val url: UrlDto? = null,
)

@Serializable
internal data class UrlDto(val resource: String)
