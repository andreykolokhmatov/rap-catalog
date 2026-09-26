package band.effective.education.rapcatalog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import band.effective.education.rapcatalog.resources.Res
import band.effective.education.rapcatalog.resources.action_back
import band.effective.education.rapcatalog.resources.app_title
import org.jetbrains.compose.resources.stringResource

/**
 * Шапка приложения и место под содержимое экрана.
 *
 * Шапка залита тем же цветом, что и фон: отдельная полоса сверху крала бы
 * внимание у списка, ради которого экран и открывают.
 *
 * Кнопка «назад» появляется, только когда есть куда возвращаться: [onBack] равен
 * `null`, пока в стеке один экран. В слот [actions] уезжает то, что живёт в правом
 * углу шапки на всех экранах, — переключатели темы и языка.
 *
 * Стрелка нарисована символом, а не иконкой: набор `material-icons` в зависимости
 * не входит, а тянуть его ради одной стрелки в вехе про каркас незачем. Подпись
 * для скринридера всё равно берётся из ресурсов, иначе кнопка-иконка остаётся
 * безымянной.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (Modifier) -> Unit,
) {
    val backLabel = stringResource(Res.string.action_back)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.app_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.semantics { contentDescription = backLabel },
                        ) {
                            Text("←", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        },
    ) { insets ->
        Box(Modifier.padding(insets)) {
            content(Modifier.fillMaxSize())
        }
    }
}
