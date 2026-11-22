package ru.omgtu.kolokhmatov.rapcatalog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistPage
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistRepository
import ru.omgtu.kolokhmatov.rapcatalog.domain.ArtistType
import ru.omgtu.kolokhmatov.rapcatalog.domain.LoadException
import ru.omgtu.kolokhmatov.rapcatalog.ui.detail.ArtistDetailState
import ru.omgtu.kolokhmatov.rapcatalog.ui.detail.ArtistDetailViewModel
import ru.omgtu.kolokhmatov.rapcatalog.ui.list.ArtistListContent
import ru.omgtu.kolokhmatov.rapcatalog.ui.list.ArtistListIntent
import ru.omgtu.kolokhmatov.rapcatalog.ui.list.ArtistListViewModel
import ru.omgtu.kolokhmatov.rapcatalog.ui.list.NextPageStatus
import ru.omgtu.kolokhmatov.rapcatalog.ui.navigation.Navigator
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {

    private fun artist(id: String) = Artist(
        id = id, name = id, sortName = id, disambiguation = "", type = ArtistType.PERSON,
        country = "", area = "", beginArea = "", lifeSpanBegin = "", lifeSpanEnd = "",
        ended = false, genres = emptyList(), tags = emptyList(), aliases = emptyList(),
        releaseGroups = emptyList(), related = emptyList(), biography = "", biographyRu = "",
        photoUrl = "",
    )

    // Репозиторий, который отвечает по сценарию и запоминает отменённые запросы.
    private class FakeRepository(
        val onArtists: suspend (query: String, offset: Int) -> ArtistPage,
        val onArtist: suspend (id: String) -> Artist? = { awaitCancellation() },
    ) : ArtistRepository {
        val cancelled = mutableListOf<String>()

        override suspend fun artists(query: String, offset: Int): ArtistPage = try {
            onArtists(query, offset)
        } catch (e: CancellationException) {
            cancelled += query
            throw e
        }

        override suspend fun artist(id: String): Artist? = try {
            onArtist(id)
        } catch (e: CancellationException) {
            cancelled += id
            throw e
        }
    }

    private fun test(body: suspend TestScope.() -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            body()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun newQueryCancelsPreviousRequestWithoutError() = test {
        val repository = FakeRepository(onArtists = { query, _ ->
            if (query.isEmpty()) awaitCancellation() else ArtistPage(listOf(artist("Nas")), total = 1)
        })
        val viewModel = ArtistListViewModel(Navigator(), repository, dispatcher = Dispatchers.Main)
        runCurrent()

        viewModel.onIntent(ArtistListIntent.QueryChanged("nas"))
        advanceUntilIdle()

        assertEquals(listOf(""), repository.cancelled)
        val content = assertIs<ArtistListContent.Loaded>(viewModel.state.value.content)
        assertEquals(listOf("Nas"), content.artists.map { it.id })
    }

    @Test
    fun loadExceptionBecomesErrorAndRetryReloads() = test {
        var fail = true
        val repository = FakeRepository(onArtists = { _, _ ->
            if (fail) throw LoadException.Server(503) else ArtistPage(listOf(artist("a")), total = 1)
        })
        val viewModel = ArtistListViewModel(Navigator(), repository, dispatcher = Dispatchers.Main)
        advanceUntilIdle()
        assertEquals(ArtistListContent.Error(ErrorReason.RATE_LIMIT), viewModel.state.value.content)

        fail = false
        viewModel.onIntent(ArtistListIntent.Retry)
        advanceUntilIdle()
        assertIs<ArtistListContent.Loaded>(viewModel.state.value.content)
    }

    @Test
    fun nextPageDropsDuplicatesAndKeepsErrorUnderList() = test {
        val repository = FakeRepository(onArtists = { _, offset ->
            when (offset) {
                0 -> ArtistPage(listOf(artist("a"), artist("b")), total = 6)
                2 -> ArtistPage(listOf(artist("b"), artist("c")), total = 6)
                else -> throw LoadException.Connection()
            }
        })
        val viewModel = ArtistListViewModel(Navigator(), repository, dispatcher = Dispatchers.Main)
        advanceUntilIdle()

        viewModel.onIntent(ArtistListIntent.EndReached)
        advanceUntilIdle()
        var content = assertIs<ArtistListContent.Loaded>(viewModel.state.value.content)
        assertEquals(listOf("a", "b", "c"), content.artists.map { it.id })
        assertEquals(4, content.nextOffset)

        // Сервер обещал больше, но следующая страница упала: список остаётся.
        viewModel.onIntent(ArtistListIntent.EndReached)
        advanceUntilIdle()
        content = assertIs<ArtistListContent.Loaded>(viewModel.state.value.content)
        assertEquals(listOf("a", "b", "c"), content.artists.map { it.id })
        assertEquals(NextPageStatus.Error(ErrorReason.CONNECTION), content.nextPage)
    }

    @Test
    fun closingDetailCancelsLoadingWithoutError() = test {
        val repository = FakeRepository(onArtists = { _, _ -> awaitCancellation() })
        val store = ViewModelStore()
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                @Suppress("UNCHECKED_CAST")
                return ArtistDetailViewModel(Navigator(), repository, id = "eminem") as T
            }
        }
        val viewModel = ViewModelProvider.create(store, factory)[ArtistDetailViewModel::class]
        runCurrent()
        assertEquals(ArtistDetailState.Loading, viewModel.state.value)

        // Так «назад» снимает экран со стека: хранилище записи очищается.
        store.clear()
        advanceUntilIdle()

        assertEquals(listOf("eminem"), repository.cancelled)
        assertEquals(ArtistDetailState.Loading, viewModel.state.value)
    }

    @Test
    fun missingArtistIsNotFound() = test {
        val repository = FakeRepository(onArtists = { _, _ -> awaitCancellation() }, onArtist = { null })
        val viewModel = ArtistDetailViewModel(Navigator(), repository, id = "missing")
        advanceUntilIdle()
        assertTrue(viewModel.state.value is ArtistDetailState.NotFound)
    }
}
