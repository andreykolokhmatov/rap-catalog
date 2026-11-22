package ru.omgtu.kolokhmatov.rapcatalog.data.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal actual val httpEngine: HttpClientEngineFactory<*> = Darwin

internal actual val canSetUserAgent: Boolean = true
