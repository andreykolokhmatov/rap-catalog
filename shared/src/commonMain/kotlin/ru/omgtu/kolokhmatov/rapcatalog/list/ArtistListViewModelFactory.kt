package ru.omgtu.kolokhmatov.rapcatalog.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository
import ru.omgtu.kolokhmatov.rapcatalog.ui.navigation.Navigator
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
