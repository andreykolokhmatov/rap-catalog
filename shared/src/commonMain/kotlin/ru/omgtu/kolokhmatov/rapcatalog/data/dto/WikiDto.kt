package ru.omgtu.kolokhmatov.rapcatalog.data.dto

import kotlinx.serialization.Serializable

// Викиданные wbgetentities: id сущности → её статьи в Википедиях.
@Serializable
internal data class WikidataEntitiesDto(
    val entities: Map<String, WikidataEntityDto> = emptyMap(),
)

@Serializable
internal data class WikidataEntityDto(
    // Ключ — вики: enwiki, ruwiki.
    val sitelinks: Map<String, SitelinkDto> = emptyMap(),
)

@Serializable
internal data class SitelinkDto(val title: String)

// Краткое содержание статьи Википедии: первый абзац в extract.
@Serializable
internal data class WikiSummaryDto(val extract: String? = null)

// SPARQL Викиданных: строки «MBID — изображение (P18)».
@Serializable
internal data class WikidataImagesDto(val results: SparqlResultsDto = SparqlResultsDto())

@Serializable
internal data class SparqlResultsDto(val bindings: List<ImageBindingDto> = emptyList())

@Serializable
internal data class ImageBindingDto(
    val mbid: SparqlValueDto,
    // http://commons.wikimedia.org/wiki/Special:FilePath/<имя файла>
    val image: SparqlValueDto,
)

@Serializable
internal data class SparqlValueDto(val value: String)
