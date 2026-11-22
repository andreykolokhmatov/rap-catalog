package ru.omgtu.kolokhmatov.rapcatalog.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.omgtu.kolokhmatov.rapcatalog.Screen
import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository
import ru.omgtu.kolokhmatov.rapcatalog.domain.LoadException
import ru.omgtu.kolokhmatov.rapcatalog.ui.navigation.Navigator
import ru.omgtu.kolokhmatov.rapcatalog.ui.toReason
import kotlin.time.Duration.Companion.milliseconds

// Запрос уходит, когда пользователь перестал печатать: MusicBrainz пускает раз в секунду.
private val SEARCH_DELAY = 400.milliseconds

class ArtistListViewModel(
    private val navigator: Navigator,
    private val repository: ArtistRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val _state = MutableStateFlow(ArtistListState())
    val state: StateFlow<ArtistListState> = _state.asStateFlow()

    // Не data class: каждый запрос — новое значение, даже с тем же текстом.
    // Иначе StateFlow проглотил бы «Повторить» как повтор старого значения.
    private class FirstPage(val query: String, val debounce: Boolean)

    private val firstPage = MutableStateFlow(FirstPage(query = "", debounce = false))
    private var nextPageJob: Job? = null

    init {
        viewModelScope.launch {
            // collectLatest отменяет прошлую загрузку, как только пришёл новый
            // запрос: и паузу ввода, и запрос в сеть. Отмена — не ошибка, её
            // никто не ловит, и на экране она не появляется.
            firstPage.collectLatest { request ->
                if (request.debounce) delay(SEARCH_DELAY)
                loadFirstPage(request.query)
            }
        }
    }

    fun onIntent(intent: ArtistListIntent) {
        when (intent) {
            is ArtistListIntent.CardClicked -> navigator.addToBackStack(Screen.Detail(intent.id))
            is ArtistListIntent.QueryChanged -> {
                if (intent.query == _state.value.query) return
                nextPageJob?.cancel()
                _state.update { it.copy(query = intent.query, content = ArtistListContent.Loading) }
                firstPage.value = FirstPage(intent.query, debounce = true)
            }
            ArtistListIntent.Retry -> firstPage.value = FirstPage(_state.value.query, debounce = false)
            ArtistListIntent.EndReached -> loadNextPage()
            ArtistListIntent.RetryNextPage -> {
                val loaded = _state.value.content as? ArtistListContent.Loaded ?: return
                if (loaded.nextPage !is NextPageStatus.Error) return
                setContent(loaded.copy(nextPage = NextPageStatus.Idle))
                loadNextPage()
            }
        }
    }

    private suspend fun loadFirstPage(query: String) {
        nextPageJob?.cancel()
        setContent(ArtistListContent.Loading)
        val content = try {
            val page = repository.artists(query, offset = 0)
            if (page.artists.isEmpty()) {
                ArtistListContent.NotFound
            } else {
                ArtistListContent.Loaded(
                    query = query,
                    artists = page.artists,
                    total = page.total,
                    nextOffset = page.artists.size,
                )
            }
        } catch (e: LoadException) {
            ArtistListContent.Error(e.toReason())
        }
        setContent(content)
    }

    // Догрузка при прокрутке. После ошибки сама не повторяет: иначе каждое
    // движение у конца списка слало бы новый запрос. Повтор — кнопкой.
    private fun loadNextPage() {
        val current = _state.value.content as? ArtistListContent.Loaded ?: return
        if (!current.hasMore || current.nextPage != NextPageStatus.Idle) return
        setContent(current.copy(nextPage = NextPageStatus.Loading))

        nextPageJob = viewModelScope.launch {
            val next = try {
                val page = repository.artists(current.query, offset = current.nextOffset)
                val artists = withContext(dispatcher) { current.artists.appendNew(page.artists) }
                current.copy(
                    artists = artists,
                    // Пустая страница — сервер отдал всё, хоть count и обещал больше.
                    total = if (page.artists.isEmpty()) current.nextOffset else page.total,
                    nextOffset = current.nextOffset + page.artists.size,
                    nextPage = NextPageStatus.Idle,
                )
            } catch (e: LoadException) {
                current.copy(nextPage = NextPageStatus.Error(e.toReason()))
            }
            // Пока грузилась страница, могли начать новый поиск: тогда она уже чужая.
            if (firstPage.value.query == current.query) setContent(next)
        }
    }

    private fun setContent(content: ArtistListContent) {
        _state.update { it.copy(content = content) }
    }
}

// Склейка страниц: сервер сортирует по релевантности, и запись может
// переехать на соседнюю страницу. Повторы отбрасываются по MBID.
private fun List<Artist>.appendNew(page: List<Artist>): List<Artist> {
    val seen = mapTo(HashSet(size + page.size)) { it.id }
    return this + page.filter { seen.add(it.id) }
}
