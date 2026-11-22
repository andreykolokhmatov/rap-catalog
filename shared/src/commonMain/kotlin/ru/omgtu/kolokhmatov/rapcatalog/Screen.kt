package ru.omgtu.kolokhmatov.rapcatalog

sealed interface Screen {
    data object List : Screen
    // instance различает записи одного артиста в стеке (A → B → A): у каждой
    // своё хранилище ViewModel и своё сохранённое состояние. Ставит Navigator.
    data class Detail(val id: String, val instance: Int = 0) : Screen
}
