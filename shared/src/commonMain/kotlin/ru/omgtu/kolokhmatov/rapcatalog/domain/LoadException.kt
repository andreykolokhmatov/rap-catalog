package ru.omgtu.kolokhmatov.rapcatalog.domain

// Ошибка загрузки, которую data-слой отдаёт наверх вместо исключений Ktor и
// сериализации. ViewModel ловит только её: всё остальное, в том числе отмена,
// проходит мимо.
sealed class LoadException(cause: Throwable? = null) : Exception(cause) {
    // Сервер недоступен: нет сети, неверный адрес, таймаут, CORS.
    class Connection(cause: Throwable? = null) : LoadException(cause)

    // Сервер ответил ошибкой. 503 у MusicBrainz — превышен лимит запросов.
    class Server(val status: Int) : LoadException()

    // Ответ пришёл, но не разбирается: не JSON, нет обязательного поля.
    class BadResponse(cause: Throwable? = null) : LoadException(cause)

    // Запрошенной записи на сервере нет.
    class NotFound : LoadException()
}
