package band.effective.education.rapcatalog

sealed interface Screen {
    data object List : Screen
    data class Detail(val id: String) : Screen
}
