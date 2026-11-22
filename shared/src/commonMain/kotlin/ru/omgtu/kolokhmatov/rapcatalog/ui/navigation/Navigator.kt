package ru.omgtu.kolokhmatov.rapcatalog.ui.navigation

import ru.omgtu.kolokhmatov.rapcatalog.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Navigator {
    private val _navStack: MutableStateFlow<List<Screen>> = MutableStateFlow(listOf(Screen.List))
    val navStack = _navStack.asStateFlow()

    private var nextInstance = 0

    fun addToBackStack(screen: Screen) {
        val entry = if (screen is Screen.Detail) screen.copy(instance = ++nextInstance) else screen
        _navStack.update { it + entry }
    }

    fun back() {
        _navStack.update { stack ->
            if (stack.size > 1) stack.dropLast(1) else stack
        }
    }
}
