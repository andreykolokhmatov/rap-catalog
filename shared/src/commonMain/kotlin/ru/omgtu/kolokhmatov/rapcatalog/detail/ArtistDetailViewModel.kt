package ru.omgtu.kolokhmatov.rapcatalog.detail

import androidx.lifecycle.ViewModel
import ru.omgtu.kolokhmatov.rapcatalog.Screen
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository
import ru.omgtu.kolokhmatov.rapcatalog.ui.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ArtistDetailViewModel(
    private val navigator: Navigator,
    repository: ArtistRepository,
    id: String,
) : ViewModel() {
    private val _state = MutableStateFlow(
        repository.byId(id)?.let { artist ->
            ArtistDetailState(artist = artist, related = repository.related(artist))
        },
    )
    val state: StateFlow<ArtistDetailState?> = _state.asStateFlow()

    fun onIntent(intent: ArtistDetailIntent) {
        when (intent) {
            is ArtistDetailIntent.RelatedArtistClicked ->
                navigator.addToBackStack(Screen.Detail(intent.id))
        }
    }
}
