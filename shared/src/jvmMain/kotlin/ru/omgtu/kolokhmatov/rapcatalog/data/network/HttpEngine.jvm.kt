package ru.omgtu.kolokhmatov.rapcatalog.data.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.java.Java

internal actual val httpEngine: HttpClientEngineFactory<*> = Java

internal actual val canSetUserAgent: Boolean = true
