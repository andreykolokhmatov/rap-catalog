package ru.omgtu.kolokhmatov.rapcatalog.domain

// Ошибки загрузки приходят как LoadException. Отмена корутины — не ошибка:
// CancellationException реализация пропускает наверх как есть.
interface ArtistRepository {
    // Пустой query — весь каталог; offset — сколько записей уже показано.
    suspend fun artists(query: String, offset: Int): ArtistPage

    // null — такой записи в MusicBrainz нет.
    suspend fun artist(id: String): Artist?

    companion object {
        const val PAGE_SIZE = 30
    }
}
