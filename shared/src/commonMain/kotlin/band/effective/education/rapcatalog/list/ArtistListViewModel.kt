package band.effective.education.rapcatalog.list

import androidx.lifecycle.ViewModel
import band.effective.education.rapcatalog.Screen
import band.effective.education.rapcatalog.domain.ArtistRepository
import band.effective.education.rapcatalog.ui.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Единственное место, где состояние списка меняется.
 *
 * Наружу отдаётся [StateFlow] только на чтение: изменить состояние снаружи нельзя,
 * можно лишь прислать намерение в [onIntent]. `ViewModel` здесь гугловская, из
 * `androidx.lifecycle`, и она мультиплатформенная — тот же класс работает на
 * Android, desktop и web.
 *
 * Данные берутся из [ArtistRepository]. В В2 репозиторий начнёт ходить в сеть,
 * и этот файл не изменится.
 */
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
