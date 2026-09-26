package band.effective.education.rapcatalog.detail

import androidx.lifecycle.ViewModel
import band.effective.education.rapcatalog.Screen
import band.effective.education.rapcatalog.domain.ArtistRepository
import band.effective.education.rapcatalog.ui.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Состояние одной карточки.
 *
 * Состояние объявлено как `ArtistDetailState?`: записи с таким MBID может не быть,
 * и экран обязан это пережить, а не упасть. В В2, когда данные придут из сети,
 * сюда же добавятся «грузится» и «не смогли загрузить».
 */
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
