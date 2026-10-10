package ru.omgtu.kolokhmatov.rapcatalog.ui.list

import androidx.lifecycle.ViewModel
import ru.omgtu.kolokhmatov.rapcatalog.Screen
import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository
import ru.omgtu.kolokhmatov.rapcatalog.ui.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArtistListViewModel(
    private val navigator: Navigator,
    repository: ArtistRepository,
) : ViewModel() {
    private val all: List<Artist> = repository.all()

    private val _state = MutableStateFlow(ArtistListState(items = all))
    val state: StateFlow<ArtistListState> = _state.asStateFlow()

    fun onIntent(intent: ArtistListIntent) {
        when (intent) {
            is ArtistListIntent.CardClicked -> navigator.addToBackStack(Screen.Detail(intent.id))
            is ArtistListIntent.QueryChanged -> _state.update {
                it.copy(query = intent.query, items = filter(intent.query))
            }
        }
    }

    private fun filter(query: String): List<Artist> {
        val needle = query.trim()
        if (needle.isEmpty()) return all
        return all.filter { it.name.contains(needle, ignoreCase = true) }
    }
}
