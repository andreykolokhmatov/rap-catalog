package ru.omgtu.kolokhmatov.rapcatalog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.omgtu.kolokhmatov.rapcatalog.resources.Res
import ru.omgtu.kolokhmatov.rapcatalog.resources.action_retry
import ru.omgtu.kolokhmatov.rapcatalog.resources.loading
import ru.omgtu.kolokhmatov.rapcatalog.ui.ErrorReason
import ru.omgtu.kolokhmatov.rapcatalog.ui.errorMessage
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier, size: Dp = 40.dp) {
    val label = stringResource(Res.string.loading)
    Box(modifier = modifier.padding(24.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size)
                .semantics { contentDescription = label },
        )
    }
}

@Composable
fun ErrorMessage(
    reason: ErrorReason,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Box(modifier = modifier.padding(if (compact) 12.dp else 32.dp), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.widthIn(max = 420.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 16.dp),
        ) {
            Text(
                text = stringResource(errorMessage(reason)),
                style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            FilledTonalButton(onClick = onRetry) {
                Text(stringResource(Res.string.action_retry))
            }
        }
    }
}
