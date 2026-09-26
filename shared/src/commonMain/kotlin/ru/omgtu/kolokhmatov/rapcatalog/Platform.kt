package ru.omgtu.kolokhmatov.rapcatalog

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
