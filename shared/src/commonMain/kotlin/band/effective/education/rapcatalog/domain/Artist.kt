package band.effective.education.rapcatalog.domain

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
    val relatedArtistIds: List<String>,
    val biography: String,
    val biographyRu: String,
)

enum class ArtistType { PERSON, GROUP, OTHER }

data class Tag(val name: String, val count: Int)

data class Alias(val name: String, val primary: Boolean)

data class ReleaseGroup(
    val id: String,
    val title: String,
    val firstReleaseDate: String,
    val primaryType: String,
) {
    val releaseYear: String get() = firstReleaseDate.take(4)
}
