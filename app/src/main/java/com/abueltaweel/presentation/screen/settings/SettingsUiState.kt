package com.abueltaweel.presentation.screen.settings

import com.abueltaweel.R
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.presentation.screen.settings.SelectionItem

data class SettingsUiState(
    val sections: List<SettingsSectionUiState> = emptyList(),
    val selectedLanguage: Language = Language.ARABIC,
    val selectedTheme: ThemeState = ThemeState.SYSTEM,
    val selectedMadhab: MadhabState = MadhabState.SHAFI,
    val selectedCalculationMethod: CalculationMethod = CalculationMethod.EGYPTIAN,
    val location: LocationUiState = LocationUiState(),
    val dialog: SelectionDialogUiState? = null,
    val selectedFontSize: QuranFontSize = QuranFontSize.SMALL,
    // مؤذن مستقل لكل صلاة
    val prayerMoazens: Map<Prayer.PrayerName, Moazen> = Prayer.PrayerName.entries
        .associateWith { Moazen.AZAN_MAKKAH },
    val activeMoazenPrayer: Prayer.PrayerName? = null,
    val selectedTafseer: TafseerType = TafseerType.MOKHTASAR
) {
    data class LocationUiState(
        val country: String = "Unknown",
        val city: String = "Unknown"
    )

    data class SettingsSectionUiState(
        val titleRes: Int,
        val items: List<SettingsItemUiState>
    )

    data class SettingsItemUiState(
        val icon: Int,
        val title: Int,
        val description: Int = 0,
        val descriptionText: String? = null,
        val action: SettingsAction
    )

    enum class ThemeState(val value: Int) {
        LIGHT(R.string.light),
        DARK(R.string.dark),
        SYSTEM(R.string.system_default)
    }

    enum class MadhabState(val value: Int) {
        SHAFI(R.string.shafi),
        HANAFI(R.string.hanafi)
    }

    enum class Language(val nameRes: Int) {
        ENGLISH(R.string.english),
        ARABIC(R.string.arabic)
    }

    enum class SelectionDialogType {
        LANGUAGE,
        THEME,
        MADHAB,
        CALCULATION_METHOD,
        MOAZEN,
        TAFSEER,
        FONT_SIZE
    }

    data class SelectionDialogUiState(
        val titleRes: Int,
        val descriptionRes: Int? = null,
        val options: List<SelectionItem>,
        val selectedIndex: Int,
        val type: SelectionDialogType
    )

    enum class CalculationMethod(val value: Int) {
        MUSLIM_WORLD_LEAGUE(R.string.muslim_world_league),
        EGYPTIAN(R.string.egyptian),
        KARACHI(R.string.karachi),
        UMM_AL_QURA(R.string.umm_al_qura),
        DUBAI(R.string.dubai),
        QATAR(R.string.qatar),
        KUWAIT(R.string.kuwait),
        MOONSIGHTING_COMMITTEE(R.string.moonsighting_committee),
        SINGAPORE(R.string.singapore),
        NORTH_AMERICA(R.string.north_america)
    }

    enum class SettingsAction {
        LANGUAGE,
        THEME,
        LOCATION,
        CALCULATION_METHOD,
        MOAZEN_FAJR,
        MOAZEN_ZUHR,
        MOAZEN_ASR,
        MOAZEN_MAGHRIB,
        MOAZEN_ISHA,
        TEXT_FONT,
        TAFSEER,
        MADHAB,
    }

    enum class QuranFontSize(val value: Int, val sizeSp: Int) {
        SMALL(R.string.small, 20),
        MEDIUM(R.string.medium, 24),
        LARGE(R.string.large, 28),
        EXTRA_LARGE(R.string.extra_large, 32)
    }

    enum class Moazen(val fileName: String, val id: Int) {
    AZAN_ABED_ALBASET("azan_abed_albaset.mp3", 0),
    AZAN_MAKKAH("azan_makkah.mp3", 1),
    AZAN_MANSOOR_AL_ZAHRANI("azan_mansoor_al_zahrani.mp3", 2),
    AZAN_MISHARY_ALAFASI("azan_mishary_alafasi.mp3", 3),
    AZAN_MOHAMMED_ALMENSHWY("azan_mohammed_almenshawy.mp3", 4),
    AZAN_NASSER_ALQATAMI("azan_nasser_alqatami.mp3", 5),
    AZAN_SUHAIB_KHATBA("azan_suhaib_khatba.mp3", 6),
    AZAN_MOHAMED_REFAT("azan_mohamed_refat.mp3", 7),
    AZAN_AL_NAKSHABANDY("azan_al_nakshabandy.mp3", 8),
    AZAN_AL_HOSARY("azan_al_hosary.mp3", 9)
}
    

    enum class TafseerType(val value: Int, val fileName: String) {
        MOKHTASAR(R.string.tafseer_mokhtasar, "tf_ab_mokhtasar_ar.json"),
        MOYASSAR(R.string.tafseer_moyasser, "tf_moyasser.json")
    }
}
