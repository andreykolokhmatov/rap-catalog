package band.effective.education.rapcatalog.domain

/**
 * Одна запись каталога — артист.
 *
 * Поля названы так же, как в ответе MusicBrainz
 * (`/ws/2/artist/{mbid}?inc=aliases+genres+tags+release-groups+artist-rels`),
 * чтобы в В2 сюда встала сериализация без переименований. Дефисы в именах API
 * (`sort-name`, `life-span`, `begin-area`) закроются там `@SerialName`.
 */
data class Artist(
    /** MBID — UUID-строка, а не число. Первичный ключ каталога и аргумент маршрута. */
    val id: String,
    val name: String,
    /** `sort-name`: «Lamar, Kendrick». Порядок списка строится по нему. */
    val sortName: String,
    /** `disambiguation`: «US rapper» — короткое уточнение, кто это. Часто пустое. */
    val disambiguation: String,
    /** `type`: один человек или состав. */
    val type: ArtistType,
    /** ISO-код страны: «US». Не переводится — это содержимое каталога. */
    val country: String,
    /** `area.name`: «United States». */
    val area: String,
    /** `begin-area.name`: «Brooklyn» — откуда родом. */
    val beginArea: String,
    /**
     * `life-span.begin`: год или полная дата, как отдал API. Пусто = неизвестно.
     *
     * Значит разное в зависимости от [type]: у `PERSON` это дата рождения,
     * а не начало карьеры; у `GROUP` — год, когда состав собрался. Подпись на
     * экране выбирается по типу, одной формулировкой это не покрыть.
     */
    val lifeSpanBegin: String,
    /** `life-span.end`: для `PERSON` — дата смерти, для `GROUP` — распад. */
    val lifeSpanEnd: String,
    /** `life-span.ended`. Живой артист и распавшаяся группа рисуются по-разному. */
    val ended: Boolean,
    /** Жанры. Аналог «типов» в эталонном каталоге: по ним и чипы, и палитра. */
    val genres: List<Genre>,
    /** `tags[]`: имя + сколько людей проголосовало. Числа рисуются полосами. */
    val tags: List<Tag>,
    /** `aliases[]`: псевдонимы. «Makaveli», «Guwop». */
    val aliases: List<Alias>,
    /** `release-groups[]`: дискография. Порядок — как пришёл, сортируем на экране. */
    val releaseGroups: List<ReleaseGroup>,
    /**
     * Связанные артисты из `relations` (`artist-rels`): состав группы, коллаборации,
     * общая сцена. Хранятся MBID-ами — по ним экран детали уходит на другую запись.
     */
    val relatedArtistIds: List<String>,
    /**
     * Короткая справка об артисте — первый абзац статьи в Википедии.
     *
     * Единственное поле, которое приходит **не** из MusicBrainz: там для этого есть
     * `annotation`, но это служебная заметка редакторов, а не текст для читателя —
     * у Eminem в ней просьба не заводить «Relapse: Refill» отдельным релизом.
     *
     * В В2 достаётся в два шага, и оба звена настоящие: у записи MusicBrainz есть
     * связь с Викиданными (`inc=url-rels`), у элемента Викиданных — ссылка на
     * статью, а у Википедии есть `REST /page/summary/{title}`.
     *
     * Лицензия CC BY-SA 4.0, атрибуция — в `docs/CREDITS.md`.
     * Пустая строка — законное значение, раздел тогда не рисуется.
     */
    val biography: String,
    /**
     * Та же справка из русской Википедии.
     *
     * Единственное место, где содержимое каталога существует на двух языках.
     * Остальные данные — имена, страны, названия альбомов — остаются как пришли
     * из MusicBrainz: там русской локали нет вовсе.
     *
     * Это не перевод английского текста, а отдельная статья: у русской Википедии
     * свои авторы и своя редакция. Пусто — показывается [biography].
     */
    val biographyRu: String,
)

/** Кто это: один человек или состав. `Other` — всё, что API не отнёс ни туда, ни туда. */
enum class ArtistType { PERSON, GROUP, OTHER }

/** Тег с числом голосов. [count] — то, что рисуется полосой. */
data class Tag(val name: String, val count: Int)

/** Псевдоним. [primary] — основной ли он по версии MusicBrainz. */
data class Alias(val name: String, val primary: Boolean)

/**
 * Релиз-группа: альбом, сингл, EP.
 *
 * [firstReleaseDate] приходит строкой переменной точности — «1996», «1996-02-13».
 * Год достаётся [releaseYear], полную дату не разбираем: в В1 нет kotlinx-datetime.
 */
data class ReleaseGroup(
    val id: String,
    val title: String,
    val firstReleaseDate: String,
    val primaryType: String,
) {
    val releaseYear: String get() = firstReleaseDate.take(4)
}
