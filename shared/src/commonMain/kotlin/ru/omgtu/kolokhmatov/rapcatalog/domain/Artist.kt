package ru.omgtu.kolokhmatov.rapcatalog.domain

data class Artist(
    val id: String,
    val name: String,
    val sortName: String,
    val disambiguation: String,
    val type: ArtistType,
    val country: String,
    val area: String,
    val beginArea: String,
    val lifeSpanBegin: String,
    val lifeSpanEnd: String,
    val ended: Boolean,
    val genres: List<Genre>,
    val tags: List<Tag>,
    val aliases: List<Alias>,
    val releaseGroups: List<ReleaseGroup>,
    // Участники, группы и коллабы — краткие записи из связей карточки:
    // полная карточка связанного артиста грузится при переходе на неё.
    val related: List<Artist>,
    val biography: String,
    val biographyRu: String,
    // Миниатюра с Викисклада; пустая строка — фото нет, рисуется буква.
    val photoUrl: String,
)

enum class ArtistType { PERSON, GROUP, OTHER }

data class Tag(val name: String, val count: Int)

// primary в MusicBrainz — основной для своего языка locale, а не вообще:
// у Eminem основные и «Eminem» (en), и «エミネム» (ja).
data class Alias(val name: String, val primary: Boolean, val locale: String)

data class ReleaseGroup(
    val id: String,
    val title: String,
    val firstReleaseDate: String,
    val primaryType: String,
) {
    val releaseYear: String get() = firstReleaseDate.take(4)
}
