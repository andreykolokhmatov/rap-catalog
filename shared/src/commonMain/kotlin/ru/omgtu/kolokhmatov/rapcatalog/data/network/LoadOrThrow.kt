package ru.omgtu.kolokhmatov.rapcatalog.data.network

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import ru.omgtu.kolokhmatov.rapcatalog.domain.LoadException

// Переводит исключения Ktor и сериализации в LoadException. Отмена не ошибка:
// CancellationException уходит наверх нетронутой, иначе экран, который
// закрыли посреди загрузки, нарисовал бы «нет связи».
internal suspend inline fun <T> loadOrThrow(
    notFound: Set<Int> = setOf(404),
    request: () -> T,
): T = try {
    request()
} catch (e: CancellationException) {
    throw e
} catch (e: ResponseException) {
    val status = e.response.status.value
    throw if (status in notFound) LoadException.NotFound() else LoadException.Server(status)
} catch (e: ContentConvertException) {
    throw LoadException.BadResponse(e)
} catch (e: SerializationException) {
    throw LoadException.BadResponse(e)
} catch (e: NoTransformationFoundException) {
    // Тело не JSON: например, HTML-страница прокси вместо ответа API.
    throw LoadException.BadResponse(e)
} catch (e: Throwable) {
    // Остальное — сбой связи: IOException и таймауты на JVM, а движок fetch
    // в браузере бросает обычный Error без своего типа.
    throw LoadException.Connection(e)
}
