package ru.omgtu.kolokhmatov.rapcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSUserDefaults

actual object PlatformLocale {
    private val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.RU }

    @Composable
    actual fun provides(language: AppLanguage): ProvidedValue<*> {
        NSUserDefaults.standardUserDefaults.setObject(listOf(language.code), "AppleLanguages")
        return LocalAppLanguage provides language
    }
}
