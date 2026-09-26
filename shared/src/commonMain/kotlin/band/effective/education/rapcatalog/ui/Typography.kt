package band.effective.education.rapcatalog.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Шкала размеров — модульная, а не «на глаз»: 12 / 14 / 16 / 18 / 22 / 32.
 *
 * Произвольные размеры ломают сканирование: глаз перестаёт отличать заголовок
 * от подписи, и список читается медленнее. Поэтому размеров ровно шесть, и
 * каждый занят своей ролью.
 *
 * Своих шрифтовых файлов здесь нет намеренно. Google отдаёт Inter и Space Grotesk
 * только вариативными TTF, их в Compose надо подключать через `FontVariation`,
 * а это лишний риск на всех пяти таргетах ради вехи про каркас. Характер набирается
 * весом и трекингом поверх системного шрифта.
 */
val AppTypography = Typography(
    // Имя артиста на экране детали — единственное место с крупным кеглем.
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.8).sp,
    ),
    // Заголовок в шапке.
    titleLarge = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.3).sp,
    ),
    // Заголовок раздела на детали.
    titleMedium = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
    ),
    // Имя артиста в карточке списка.
    titleSmall = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    // Основной текст: 14/20 — line-height 1.5, как требует читаемость.
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 18.sp),
    // Подписи: разрядка вместо уменьшения кегля — ниже 12sp опускаться нельзя.
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    // Годы релизов и веса тегов стоят колонкой. `tnum` даёт цифрам одинаковую
    // ширину, и колонка перестаёт «дышать» от строки к строке.
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.4.sp,
        fontFeatureSettings = "tnum",
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.8.sp,
    ),
)
