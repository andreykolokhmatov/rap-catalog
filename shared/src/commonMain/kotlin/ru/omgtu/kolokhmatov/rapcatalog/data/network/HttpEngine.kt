package ru.omgtu.kolokhmatov.rapcatalog.data.network

import io.ktor.client.engine.HttpClientEngineFactory

// Движок HTTP у каждого таргета свой: OkHttp на Android, HttpClient из JDK
// на desktop, fetch в браузере, NSURLSession на iOS.
internal expect val httpEngine: HttpClientEngineFactory<*>

// Браузер не даёт задать User-Agent: там уходит его собственный.
internal expect val canSetUserAgent: Boolean
