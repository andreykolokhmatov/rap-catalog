package band.effective.education.rapcatalog.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import band.effective.education.rapcatalog.domain.ArtistRepository
import band.effective.education.rapcatalog.ui.navigation.Navigator
import kotlin.reflect.KClass

class ArtistDetailViewModelFactory(
    private val navigator: Navigator,
    private val repository: ArtistRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        val id = requireNotNull(extras[ARTIST_ID_KEY]) {
            "ArtistDetailViewModel создаётся без MBID: передайте extrasFor(id)"
        }
        @Suppress("UNCHECKED_CAST")
        return ArtistDetailViewModel(
            navigator = navigator,
            repository = repository,
            id = id,
        ) as T
    }

    companion object {
        private val ARTIST_ID_KEY = CreationExtras.Key<String>()

        fun extrasFor(id: String): CreationExtras =
            MutableCreationExtras().apply { set(ARTIST_ID_KEY, id) }
    }
}
