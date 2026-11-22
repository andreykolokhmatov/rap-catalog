package ru.omgtu.kolokhmatov.rapcatalog.domain

// Страница каталога и сколько всего записей по запросу на сервере.
data class ArtistPage(
    val artists: List<Artist>,
    val total: Int,
)
