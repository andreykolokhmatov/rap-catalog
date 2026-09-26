package ru.omgtu.kolokhmatov.rapcatalog

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.omgtu.kolokhmatov.rapcatalog.resources.Res
import ru.omgtu.kolokhmatov.rapcatalog.resources.app_title
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        // Заголовок окна — тоже подпись интерфейса, значит живёт в ресурсах.
        title = stringResource(Res.string.app_title),
    ) {
        App()
    }
}
