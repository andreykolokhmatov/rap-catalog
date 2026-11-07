package ru.omgtu.kolokhmatov.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf

actual object PlatformLocale {
    private val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

    @Composable
    actual fun provides(language: AppLanguage): ProvidedValue<*> =
        LocalAppLanguage provides language
}
