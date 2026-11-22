package ru.omgtu.kolokhmatov.rapcatalog.ui.list

import androidx.compose.runtime.Immutable
import ru.omgtu.kolokhmatov.rapcatalog.domain.Artist
import ru.omgtu.kolokhmatov.rapcatalog.ui.ErrorReason

// Строка поиска меняется сразу при вводе, содержимое — когда придёт ответ.
@Immutable
data class ArtistListState(
    val query: String = "",
    val content: ArtistListContent = ArtistListContent.Loading,
)

@Immutable
sealed interface ArtistListContent {
    data object Loading : ArtistListContent

    data object NotFound : ArtistListContent

    data class Error(val reason: ErrorReason) : ArtistListContent

    data class Loaded(
        // Запрос, к которому относится список: догрузка берёт его отсюда.
        val query: String,
        val artists: List<Artist>,
        // Сколько записей по запросу на сервере.
        val total: Int,
        // С какой позиции просить следующую страницу. Не равен artists.size:
        // повторы между страницами отбрасываются.
        val nextOffset: Int,
        val nextPage: NextPageStatus = NextPageStatus.Idle,
    ) : ArtistListContent {
        val hasMore: Boolean get() = nextOffset < total
    }
}

// Догрузка следующей страницы под уже показанным списком.
@Immutable
sealed interface NextPageStatus {
    data object Idle : NextPageStatus

    data object Loading : NextPageStatus

    data class Error(val reason: ErrorReason) : NextPageStatus
}
