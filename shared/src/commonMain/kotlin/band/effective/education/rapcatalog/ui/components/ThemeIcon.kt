package band.effective.education.rapcatalog.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Солнце и месяц, нарисованные вручную.
 *
 * Символами `☀` и `☾` это делать нельзя: в шрифте целевой платформы их может
 * не оказаться, и вместо кнопки пользователь видит пустой квадрат — ровно это
 * и происходит в браузере. Векторная отрисовка не зависит ни от шрифта, ни от
 * таргета, а набор `material-icons` в зависимостях проекта отсутствует.
 *
 * Значок декоративный: подпись для скринридера висит на самой кнопке.
 */
@Composable
fun ThemeIcon(darkTheme: Boolean, modifier: Modifier = Modifier) {
    val tint = LocalContentColor.current
    Canvas(
        modifier = modifier
            .size(20.dp)
            // Вырез месяца делается режимом Clear, а он работает только внутри
            // собственного слоя: без него дыра прошла бы сквозь всю шапку.
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
    ) {
        val r = size.minDimension / 2f
        val center = Offset(r, r)
        if (darkTheme) {
            // Солнце: диск и восемь лучей — «нажми, чтобы стало светло».
            drawCircle(color = tint, radius = r * 0.45f, center = center)
            repeat(8) { i ->
                val angle = (i * 45f) * (3.14159265f / 180f)
                val from = Offset(
                    center.x + r * 0.68f * kotlin.math.cos(angle),
                    center.y + r * 0.68f * kotlin.math.sin(angle),
                )
                val to = Offset(
                    center.x + r * 0.95f * kotlin.math.cos(angle),
                    center.y + r * 0.95f * kotlin.math.sin(angle),
                )
                drawLine(color = tint, start = from, end = to, strokeWidth = r * 0.16f)
            }
        } else {
            // Месяц: круг, из которого вырезан второй круг со смещением.
            drawCircle(color = tint, radius = r * 0.85f, center = center)
            drawCircle(
                color = Color.Transparent,
                radius = r * 0.72f,
                center = Offset(center.x + r * 0.42f, center.y - r * 0.26f),
                blendMode = BlendMode.Clear,
            )
        }
    }
}

/**
 * Рамка вокруг кода языка.
 *
 * Без неё «EN» в углу шапки читается как обрывок текста, а не как кнопка.
 */
@Composable
fun LanguageFrame(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val tint = LocalContentColor.current
    androidx.compose.foundation.layout.Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(34.dp, 22.dp)) {
            drawRoundRect(
                color = tint.copy(alpha = 0.5f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                style = Stroke(width = 1.dp.toPx()),
            )
        }
        content()
    }
}
