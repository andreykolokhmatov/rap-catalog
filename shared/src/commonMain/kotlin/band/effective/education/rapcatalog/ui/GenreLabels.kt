package band.effective.education.rapcatalog.ui

import band.effective.education.rapcatalog.domain.ArtistType
import band.effective.education.rapcatalog.domain.Genre
import band.effective.education.rapcatalog.resources.Res
import band.effective.education.rapcatalog.resources.artist_type_group
import band.effective.education.rapcatalog.resources.artist_type_other
import band.effective.education.rapcatalog.resources.artist_type_person
import band.effective.education.rapcatalog.resources.genre_alternative
import band.effective.education.rapcatalog.resources.genre_boom_bap
import band.effective.education.rapcatalog.resources.genre_cloud_rap
import band.effective.education.rapcatalog.resources.genre_conscious
import band.effective.education.rapcatalog.resources.genre_crunk
import band.effective.education.rapcatalog.resources.genre_east_coast
import band.effective.education.rapcatalog.resources.genre_emo_rap
import band.effective.education.rapcatalog.resources.genre_experimental
import band.effective.education.rapcatalog.resources.genre_g_funk
import band.effective.education.rapcatalog.resources.genre_gangsta_rap
import band.effective.education.rapcatalog.resources.genre_hardcore
import band.effective.education.rapcatalog.resources.genre_hip_hop
import band.effective.education.rapcatalog.resources.genre_instrumental
import band.effective.education.rapcatalog.resources.genre_jazz_rap
import band.effective.education.rapcatalog.resources.genre_old_school
import band.effective.education.rapcatalog.resources.genre_political
import band.effective.education.rapcatalog.resources.genre_pop_rap
import band.effective.education.rapcatalog.resources.genre_rage
import band.effective.education.rapcatalog.resources.genre_rap
import band.effective.education.rapcatalog.resources.genre_rap_rock
import band.effective.education.rapcatalog.resources.genre_southern
import band.effective.education.rapcatalog.resources.genre_trap
import band.effective.education.rapcatalog.resources.genre_trap_metal
import band.effective.education.rapcatalog.resources.genre_underground
import band.effective.education.rapcatalog.resources.genre_west_coast
import org.jetbrains.compose.resources.StringResource

fun genreLabel(genre: Genre): StringResource = when (genre) {
    Genre.HIP_HOP -> Res.string.genre_hip_hop
    Genre.RAP -> Res.string.genre_rap
    Genre.POP_RAP -> Res.string.genre_pop_rap
    Genre.EAST_COAST -> Res.string.genre_east_coast
    Genre.WEST_COAST -> Res.string.genre_west_coast
    Genre.SOUTHERN -> Res.string.genre_southern
    Genre.GANGSTA_RAP -> Res.string.genre_gangsta_rap
    Genre.G_FUNK -> Res.string.genre_g_funk
    Genre.CONSCIOUS -> Res.string.genre_conscious
    Genre.POLITICAL -> Res.string.genre_political
    Genre.HARDCORE -> Res.string.genre_hardcore
    Genre.BOOM_BAP -> Res.string.genre_boom_bap
    Genre.JAZZ_RAP -> Res.string.genre_jazz_rap
    Genre.INSTRUMENTAL -> Res.string.genre_instrumental
    Genre.EXPERIMENTAL -> Res.string.genre_experimental
    Genre.ALTERNATIVE -> Res.string.genre_alternative
    Genre.UNDERGROUND -> Res.string.genre_underground
    Genre.OLD_SCHOOL -> Res.string.genre_old_school
    Genre.RAP_ROCK -> Res.string.genre_rap_rock
    Genre.TRAP -> Res.string.genre_trap
    Genre.CRUNK -> Res.string.genre_crunk
    Genre.EMO_RAP -> Res.string.genre_emo_rap
    Genre.CLOUD_RAP -> Res.string.genre_cloud_rap
    Genre.TRAP_METAL -> Res.string.genre_trap_metal
    Genre.RAGE -> Res.string.genre_rage
}

fun artistTypeLabel(type: ArtistType): StringResource = when (type) {
    ArtistType.PERSON -> Res.string.artist_type_person
    ArtistType.GROUP -> Res.string.artist_type_group
    ArtistType.OTHER -> Res.string.artist_type_other
}
