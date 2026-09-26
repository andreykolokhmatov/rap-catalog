package band.effective.education.rapcatalog.ui

import band.effective.education.rapcatalog.domain.Artist
import band.effective.education.rapcatalog.resources.Res
import band.effective.education.rapcatalog.resources.artist_a_21_savage
import band.effective.education.rapcatalog.resources.artist_a_2pac
import band.effective.education.rapcatalog.resources.artist_a_50_cent
import band.effective.education.rapcatalog.resources.artist_a_tribe_called_quest
import band.effective.education.rapcatalog.resources.artist_beastie_boys
import band.effective.education.rapcatalog.resources.artist_busta_rhymes
import band.effective.education.rapcatalog.resources.artist_cypress_hill
import band.effective.education.rapcatalog.resources.artist_de_la_soul
import band.effective.education.rapcatalog.resources.artist_drake
import band.effective.education.rapcatalog.resources.artist_eminem
import band.effective.education.rapcatalog.resources.artist_future
import band.effective.education.rapcatalog.resources.artist_gucci_mane
import band.effective.education.rapcatalog.resources.artist_ice_cube
import band.effective.education.rapcatalog.resources.artist_j_cole
import band.effective.education.rapcatalog.resources.artist_juice_wrld
import band.effective.education.rapcatalog.resources.artist_ken_carson
import band.effective.education.rapcatalog.resources.artist_kendrick_lamar
import band.effective.education.rapcatalog.resources.artist_lil_uzi_vert
import band.effective.education.rapcatalog.resources.artist_lil_wayne
import band.effective.education.rapcatalog.resources.artist_madlib
import band.effective.education.rapcatalog.resources.artist_mf_doom
import band.effective.education.rapcatalog.resources.artist_nas
import band.effective.education.rapcatalog.resources.artist_outkast
import band.effective.education.rapcatalog.resources.artist_playboi_carti
import band.effective.education.rapcatalog.resources.artist_public_enemy
import band.effective.education.rapcatalog.resources.artist_rae_sremmurd
import band.effective.education.rapcatalog.resources.artist_run_d_m_c
import band.effective.education.rapcatalog.resources.artist_snoop_dogg
import band.effective.education.rapcatalog.resources.artist_travis_scott
import band.effective.education.rapcatalog.resources.artist_trippie_redd
import band.effective.education.rapcatalog.resources.artist_wu_tang_clan
import band.effective.education.rapcatalog.resources.artist_xxxtentacion
import band.effective.education.rapcatalog.resources.artist_ye

import org.jetbrains.compose.resources.DrawableResource

/**
 * Фотография артиста, если она есть.
 *
 * Снимки лежат локально, в composeResources: сети в этой вехе нет, и закладываться
 * на неё нельзя. Источник — Викисклад, авторы и лицензии перечислены в
 * docs/CREDITS.md.
 *
 * Ключ — MBID, а не имя: имя может смениться (Kanye West стал Ye), MBID нет.
 * Возвращает null, когда свободного снимка не нашлось, — экран рисует
 * буквенную плитку и не ломается.
 */
fun artistPhoto(artist: Artist): DrawableResource? = when (artist.id) {
    "37b2cb82-ef79-4d46-a184-a549450aa231" -> Res.drawable.artist_a_21_savage
    "382f1005-e9ab-4684-afd4-0bdae4ee37f2" -> Res.drawable.artist_a_2pac
    "8e68819d-71be-4e7d-b41d-f1df81b01d3f" -> Res.drawable.artist_a_50_cent
    "9689aa5a-4471-4fb4-9721-07cecda0fa9f" -> Res.drawable.artist_a_tribe_called_quest
    "9beb62b2-88db-4cea-801e-162cd344ee53" -> Res.drawable.artist_beastie_boys
    "10a9ff92-9637-4498-afea-7044b2ab0dc0" -> Res.drawable.artist_busta_rhymes
    "51508c1f-8d07-4a00-9cf1-26c570fe7b78" -> Res.drawable.artist_cypress_hill
    "a8ebde98-7e91-46c7-992c-90039ba42017" -> Res.drawable.artist_de_la_soul
    "9fff2f8a-21e6-47de-a2b8-7f449929d43f" -> Res.drawable.artist_drake
    "b95ce3ff-3d05-4e87-9e01-c97b66af13d4" -> Res.drawable.artist_eminem
    "48262e82-db9f-4a92-b650-dfef979b73ec" -> Res.drawable.artist_future
    "36494952-f434-45d8-a958-8b4acdbcf8a8" -> Res.drawable.artist_gucci_mane
    "1d11e2a1-4531-4d61-a8c7-7b5c6a608fd2" -> Res.drawable.artist_ice_cube
    "875203e1-8e58-4b86-8dcb-7190faf411c5" -> Res.drawable.artist_j_cole
    "4e4ebde4-0c56-4dec-844b-6c73adcdd92d" -> Res.drawable.artist_juice_wrld
    "860d7dc2-618d-437e-a3bb-7b91c2d40095" -> Res.drawable.artist_ken_carson
    "381086ea-f511-4aba-bdf9-71c753dc5077" -> Res.drawable.artist_kendrick_lamar
    "c78c3026-426b-4580-ab23-08490bb8b515" -> Res.drawable.artist_rae_sremmurd
    "8837b875-3689-4646-b3fd-d8b53815c7a8" -> Res.drawable.artist_lil_uzi_vert
    "ac9a487a-d9d2-4f27-bb23-0f4686488345" -> Res.drawable.artist_lil_wayne
    "ea9078ef-20ca-4506-81ea-2ae5fe3a42e8" -> Res.drawable.artist_madlib
    "188711ed-c99b-439c-844a-ca831f63a727" -> Res.drawable.artist_mf_doom
    "cfbc0924-0035-4d6c-8197-f024653af823" -> Res.drawable.artist_nas
    "73fdb566-a9b1-494c-9f32-51768ec9fd27" -> Res.drawable.artist_outkast
    "2baf3276-ed6a-4349-8d2e-f4601e7b2167" -> Res.drawable.artist_playboi_carti
    "bf2e15d0-4b77-469e-bfb4-f8414415baca" -> Res.drawable.artist_public_enemy
    "5ecc3f72-20a6-47a0-8dc5-fb0b3dadeea0" -> Res.drawable.artist_run_d_m_c
    "f90e8b26-9e52-4669-a5c9-e28529c47894" -> Res.drawable.artist_snoop_dogg
    "e4a51f17-a57b-47b1-b37b-f552d0f8e9e6" -> Res.drawable.artist_travis_scott
    "8032cf05-d916-4b2a-9c53-6e75d4a24bd8" -> Res.drawable.artist_trippie_redd
    "0febdcf7-4e1f-4661-9493-b40427de2c13" -> Res.drawable.artist_wu_tang_clan
    "61af87f4-16ee-4431-8504-cc06187079fb" -> Res.drawable.artist_xxxtentacion
    "164f0d73-1234-4e2c-8743-d77bf2191051" -> Res.drawable.artist_ye
    else -> null
}