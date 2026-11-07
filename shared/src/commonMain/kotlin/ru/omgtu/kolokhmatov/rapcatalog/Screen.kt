package ru.omgtu.kolokhmatov.rapcatalog

sealed interface Screen {
    data object List : Screen
    data class Detail(val id: String) : Screen
}
