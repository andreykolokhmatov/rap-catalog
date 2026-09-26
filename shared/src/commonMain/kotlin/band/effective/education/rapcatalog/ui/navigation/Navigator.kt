package band.effective.education.rapcatalog.ui.navigation

import band.effective.education.rapcatalog.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Бэкстек приложения. В Navigation 3 он — обычный список, и он ваш: графа маршрутов
 * нет, открыть экран значит добавить объект в конец списка, вернуться — убрать последний.
 *
 * Список живёт здесь, а не в `App`: ViewModel зовут [addToBackStack] и не знают
 * ни про NavDisplay, ни про то, как бэкстек нарисован.
 */
class Navigator {
    private val _navStack: MutableStateFlow<List<Screen>> = MutableStateFlow(listOf(Screen.List))
    val navStack = _navStack.asStateFlow()

    fun addToBackStack(screen: Screen) {
        _navStack.update { it + screen }
    }

    fun back() {
        _navStack.update { stack ->
            if (stack.size > 1) stack.dropLast(1) else stack
        }
    }
}
