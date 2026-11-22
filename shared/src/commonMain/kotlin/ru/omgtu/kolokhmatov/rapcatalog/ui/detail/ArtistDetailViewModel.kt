package ru.omgtu.kolokhmatov.rapcatalog.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.omgtu.kolokhmatov.rapcatalog.Screen
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository
import ru.omgtu.kolokhmatov.rapcatalog.domain.LoadException
import ru.omgtu.kolokhmatov.rapcatalog.ui.navigation.Navigator
import ru.omgtu.kolokhmatov.rapcatalog.ui.toReason

class ArtistDetailViewModel(
    private val navigator: Navigator,
    private val repository: ArtistRepository,
    private val id: String,
) : ViewModel() {
    private val _state = MutableStateFlow<ArtistDetailState>(ArtistDetailState.Loading)
    val state: StateFlow<ArtistDetailState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun onIntent(intent: ArtistDetailIntent) {
        when (intent) {
            is ArtistDetailIntent.RelatedArtistClicked ->
                navigator.addToBackStack(Screen.Detail(intent.id))
            ArtistDetailIntent.Retry -> load()
        }
    }

    // Экран сняли со стека — ViewModel очищается, viewModelScope отменяет
    // загрузку. Ловится только LoadException, отмена уходит мимо catch.
    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = ArtistDetailState.Loading
            _state.value = try {
                repository.artist(id)
                    ?.let { ArtistDetailState.Loaded(it) }
                    ?: ArtistDetailState.NotFound
            } catch (e: LoadException) {
                ArtistDetailState.Error(e.toReason())
            }
        }
    }
}
