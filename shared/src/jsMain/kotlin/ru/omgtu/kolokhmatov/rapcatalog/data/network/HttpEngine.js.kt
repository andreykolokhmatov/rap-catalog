package ru.omgtu.kolokhmatov.rapcatalog.data.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

internal actual val httpEngine: HttpClientEngineFactory<*> = Js

// User-Agent — запрещённый заголовок fetch: браузер его не пустит.
internal actual val canSetUserAgent: Boolean = false
