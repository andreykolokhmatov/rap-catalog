package ru.omgtu.kolokhmatov.rapcatalog.data.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal actual val httpEngine: HttpClientEngineFactory<*> = OkHttp

internal actual val canSetUserAgent: Boolean = true
