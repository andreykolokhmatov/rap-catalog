package band.effective.education.rapcatalog.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import band.effective.education.rapcatalog.domain.ArtistRepository
import band.effective.education.rapcatalog.ui.navigation.Navigator
import kotlin.reflect.KClass

class ArtistListViewModelFactory(
    private val navigator: Navigator,
    private val repository: ArtistRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return ArtistListViewModel(navigator = navigator, repository = repository) as T
    }
}
