package ru.omgtu.kolokhmatov.rapcatalog.list

import androidx.lifecycle.ViewModel
import ru.omgtu.kolokhmatov.rapcatalog.Screen
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository
import ru.omgtu.kolokhmatov.rapcatalog.ui.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArtistListViewModel(
    private val navigator: Navigator,
    repository: ArtistRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ArtistListState(items = repository.all()))
    val state: StateFlow<ArtistListState> = _state.asStateFlow()

    fun onIntent(intent: ArtistListIntent) {
        when (intent) {
            is ArtistListIntent.CardClicked -> navigator.addToBackStack(Screen.Detail(intent.id))
        }
    }
}
