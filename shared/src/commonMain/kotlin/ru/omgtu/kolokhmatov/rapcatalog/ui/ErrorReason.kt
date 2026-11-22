package ru.omgtu.kolokhmatov.rapcatalog.ui

import ru.omgtu.kolokhmatov.rapcatalog.domain.LoadException
import ru.omgtu.kolokhmatov.rapcatalog.resources.Res
import ru.omgtu.kolokhmatov.rapcatalog.resources.error_bad_response
import ru.omgtu.kolokhmatov.rapcatalog.resources.error_connection
import ru.omgtu.kolokhmatov.rapcatalog.resources.error_rate_limit
import ru.omgtu.kolokhmatov.rapcatalog.resources.error_server
import org.jetbrains.compose.resources.StringResource

// Что показать вместо данных. В состоянии экрана лежит причина, а не
// исключение: так состояние остаётся простым значением, и его легко сравнить.
enum class ErrorReason { CONNECTION, RATE_LIMIT, SERVER, BAD_RESPONSE }

fun LoadException.toReason(): ErrorReason = when (this) {
    is LoadException.Connection -> ErrorReason.CONNECTION
    is LoadException.Server -> if (status == 503 || status == 429) ErrorReason.RATE_LIMIT else ErrorReason.SERVER
    is LoadException.BadResponse -> ErrorReason.BAD_RESPONSE
    is LoadException.NotFound -> ErrorReason.SERVER
}

fun errorMessage(reason: ErrorReason): StringResource = when (reason) {
    ErrorReason.CONNECTION -> Res.string.error_connection
    ErrorReason.RATE_LIMIT -> Res.string.error_rate_limit
    ErrorReason.SERVER -> Res.string.error_server
    ErrorReason.BAD_RESPONSE -> Res.string.error_bad_response
}
