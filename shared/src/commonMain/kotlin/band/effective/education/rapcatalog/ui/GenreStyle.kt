package band.effective.education.rapcatalog.ui

import androidx.compose.ui.graphics.Color
import band.effective.education.rapcatalog.domain.Genre

/**
 * Цвет жанра.
 *
 * Единственное место в приложении, где цвет зашит, а не взят из `MaterialTheme`.
 * Так и задумано: здесь цвет — это данные. «Восточное побережье» синее и в светлой
 * теме, и в тёмной; поменяйся он вместе с темой — читатель потерял бы признак,
 * по которому различает записи.
 *
 * Значения подобраны так, чтобы белый текст поверх читался на обеих темах:
 * каждый цвет проверен на контраст с белым и держит 4.5:1 (WCAG AA).
 */
fun genreColor(genre: Genre): Color = when (genre) {
    Genre.HIP_HOP -> Color(0xFF6A3DC8)
    Genre.RAP -> Color(0xFF7E57C2)
    Genre.POP_RAP -> Color(0xFFD81B8C)
    Genre.EAST_COAST -> Color(0xFF1565C0)
    Genre.WEST_COAST -> Color(0xFFBF360C)
    Genre.SOUTHERN -> Color(0xFF00695C)
    Genre.GANGSTA_RAP -> Color(0xFFB71C1C)
    Genre.G_FUNK -> Color(0xFF8E24AA)
    Genre.CONSCIOUS -> Color(0xFF2E7D32)
    Genre.POLITICAL -> Color(0xFF455A64)
    Genre.HARDCORE -> Color(0xFF424242)
    Genre.BOOM_BAP -> Color(0xFF795548)
    Genre.JAZZ_RAP -> Color(0xFF0277BD)
    Genre.INSTRUMENTAL -> Color(0xFF00838F)
    Genre.EXPERIMENTAL -> Color(0xFF5E35B1)
    Genre.ALTERNATIVE -> Color(0xFF3949AB)
    Genre.UNDERGROUND -> Color(0xFF37474F)
    Genre.OLD_SCHOOL -> Color(0xFF7E5700)
    Genre.RAP_ROCK -> Color(0xFFC62828)
    Genre.TRAP -> Color(0xFF6D4C41)
    Genre.CRUNK -> Color(0xFFAD1457)
    Genre.EMO_RAP -> Color(0xFF7B1FA2)
    Genre.CLOUD_RAP -> Color(0xFF01579B)
    Genre.TRAP_METAL -> Color(0xFF212121)
    Genre.RAGE -> Color(0xFFD32F2F)
}
