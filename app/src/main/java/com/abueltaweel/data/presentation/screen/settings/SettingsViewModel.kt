package com.abueltaweel.presentation.screen.settings

import SettingsUiState
import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.viewModelScope
import com.abueltaweel.R
import com.abueltaweel.domain.entity.prayer.CalculationMethod
import com.abueltaweel.domain.entity.prayer.Madhab
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.domain.model.AppSettings
import com.abueltaweel.domain.repository.settings.SettingsRepository
import com.abueltaweel.domain.usecase.PrayerSchedulingUseCase
import com.abueltaweel.presentation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val prayerSchedulingUseCase: PrayerSchedulingUseCase
) : BaseViewModel<SettingsUiState, SettingsEffect>(SettingsUiState()),
    SettingsInteractionListener {

    private val moazenResMap = mapOf(
        SettingsUiState.Moazen.AZAN_ABED_ALBASET    to R.raw.azan_abed_albaset,
        SettingsUiState.Moazen.AZAN_MAKKAH           to R.raw.azan_makkah,
        SettingsUiState.Moazen.AZAN_MANSOOR_AL_ZAHRANI to R.raw.azan_mansoor_al_zahrani,
        SettingsUiState.Moazen.AZAN_MISHARY_ALAFASI  to R.raw.azan_mishary_alafasi,
        SettingsUiState.Moazen.AZAN_MOHAMMED_ALMENSHWY to R.raw.azan_mohammed_almenshawy,
        SettingsUiState.Moazen.AZAN_NASSER_ALQATAMI  to R.raw.azan_nasser_alqatami,
        SettingsUiState.Moazen.AZAN_SUHAIB_KHATBA    to R.raw.azan_suhaib_khatba
    )

    private var previewPlayer: MediaPlayer? = null

    init {
        observeSettings()
        observeQuranFont()
        observeTafseer()
        observeAllPrayerMoazens()
    }

    // ---- Preview ----
    fun playPreview(index: Int, context: Context) {
        stopPreview()
        val moazen = SettingsUiState.Moazen.entries.getOrNull(index) ?: return
        val resId  = moazenResMap[moazen] ?: return
        try {
            previewPlayer = MediaPlayer.create(context.applicationContext, resId)
            previewPlayer?.setOnCompletionListener { stopPreview() }
            previewPlayer?.start()
        } catch (_: Exception) {}
    }

    fun stopPreview() {
        try { previewPlayer?.stop(); previewPlayer?.release() } catch (_: Exception) {}
        previewPlayer = null
    }

    // ---- Observers ----
    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.observeAppSettings().collect { appSettings ->
                updateState { state ->
                    state.copy(
                        selectedLanguage = appSettings.language.toUi(),
                        selectedTheme = appSettings.theme.toUi(),
                        selectedMadhab = appSettings.prayerSettings.madhab.toUi(),
                        selectedCalculationMethod = appSettings.prayerSettings.calculationMethod.toUi(),
                        location = SettingsUiState.LocationUiState(
                            country = appSettings.prayerSettings.location.country,
                            city    = appSettings.prayerSettings.location.state
                        )
                    )
                }
                rebuildSections()
                prayerSchedulingUseCase.rescheduleTodayPrayerAlarms()
            }
        }
    }

    private fun observeQuranFont() {
        viewModelScope.launch {
            settingsRepository.observeQuranFontSize().collect { size ->
                val fontSize = SettingsUiState.QuranFontSize.entries
                    .firstOrNull { it.sizeSp == size } ?: SettingsUiState.QuranFontSize.MEDIUM
                updateState { it.copy(selectedFontSize = fontSize) }
                rebuildSections()
            }
        }
    }

    private fun observeTafseer() {
        viewModelScope.launch {
            settingsRepository.observeTafseer().collect { fileName ->
                val tafseer = SettingsUiState.TafseerType.entries
                    .firstOrNull { it.fileName == fileName } ?: SettingsUiState.TafseerType.MOKHTASAR
                updateState { it.copy(selectedTafseer = tafseer) }
                rebuildSections()
            }
        }
    }

    private fun observeAllPrayerMoazens() {
        viewModelScope.launch {
            settingsRepository.observeAllPrayerMoazens().collect { map ->
                val moazenMap = map.mapValues { (_, fileName) ->
                    SettingsUiState.Moazen.entries.firstOrNull { it.fileName == fileName }
                        ?: SettingsUiState.Moazen.AZAN_MAKKAH
                }
                updateState { it.copy(prayerMoazens = moazenMap) }
                rebuildSections()
            }
        }
    }

    // ---- Sections ----
    private fun rebuildSections() {
        val state = screenState.value

        fun moazenItem(prayer: Prayer.PrayerName, action: SettingsUiState.SettingsAction): SettingsUiState.SettingsItemUiState {
            val moazen = state.prayerMoazens[prayer] ?: SettingsUiState.Moazen.AZAN_MAKKAH
            val prayerNameRes = when (prayer) {
                Prayer.PrayerName.FAJR    -> R.string.fajr
                Prayer.PrayerName.ZUHR    -> R.string.dhuhr
                Prayer.PrayerName.ASR     -> R.string.asr
                Prayer.PrayerName.MAGHRIB -> R.string.maghrib
                Prayer.PrayerName.ISHA    -> R.string.isha
            }
            return SettingsUiState.SettingsItemUiState(
                icon   = R.drawable.ic_moazen,
                title  = prayerNameRes,
                description = getMoazenNameRes(moazen),
                action = action
            )
        }

        val sections = listOf(
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.general,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(R.drawable.ic_language,  R.string.language,
                        description = state.selectedLanguage.nameRes, action = SettingsUiState.SettingsAction.LANGUAGE),
                    SettingsUiState.SettingsItemUiState(R.drawable.ic_theme, R.string.theme,
                        description = state.selectedTheme.value, action = SettingsUiState.SettingsAction.THEME),
                    SettingsUiState.SettingsItemUiState(R.drawable.ic_location, R.string.location,
                        action = SettingsUiState.SettingsAction.LOCATION,
                        descriptionText = "${state.location.country}, ${state.location.city}")
                )
            ),
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.prayer,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(R.drawable.ic_calculation_method, R.string.calculation_method,
                        description = state.selectedCalculationMethod.value, action = SettingsUiState.SettingsAction.CALCULATION_METHOD),
                    SettingsUiState.SettingsItemUiState(R.drawable.ic_mosque_02, R.string.madhab,
                        description = state.selectedMadhab.value, action = SettingsUiState.SettingsAction.MADHAB)
                )
            ),
            // قسم مؤذن كل صلاة
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.moazen,
                items = listOf(
                    moazenItem(Prayer.PrayerName.FAJR,    SettingsUiState.SettingsAction.MOAZEN_FAJR),
                    moazenItem(Prayer.PrayerName.ZUHR,    SettingsUiState.SettingsAction.MOAZEN_ZUHR),
                    moazenItem(Prayer.PrayerName.ASR,     SettingsUiState.SettingsAction.MOAZEN_ASR),
                    moazenItem(Prayer.PrayerName.MAGHRIB, SettingsUiState.SettingsAction.MOAZEN_MAGHRIB),
                    moazenItem(Prayer.PrayerName.ISHA,    SettingsUiState.SettingsAction.MOAZEN_ISHA)
                )
            ),
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.quran,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(R.drawable.ic_text_font, R.string.text_font,
                        description = state.selectedFontSize.value, action = SettingsUiState.SettingsAction.TEXT_FONT),
                    SettingsUiState.SettingsItemUiState(R.drawable.ic_tafseer, R.string.al_tafseer,
                        description = getTafseerNameRes(state.selectedTafseer), action = SettingsUiState.SettingsAction.TAFSEER)
                )
            ),
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.support,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(R.drawable.ic_bug,       R.string.help_feedback, action = SettingsUiState.SettingsAction.HELP_FEEDBACK),
                    SettingsUiState.SettingsItemUiState(R.drawable.ic_star_rate, R.string.rate_app,      action = SettingsUiState.SettingsAction.RATE_APP)
                )
            )
        )
        updateState { it.copy(sections = sections) }
    }

    // ---- Dialog openers ----
    override fun onItemClick(action: SettingsUiState.SettingsAction) {
        when (action) {
            SettingsUiState.SettingsAction.LOCATION        -> sendEffect(SettingsEffect.NavigateToLocation)
            SettingsUiState.SettingsAction.HELP_FEEDBACK   -> sendEffect(SettingsEffect.NavigateToHelpFeedback)
            SettingsUiState.SettingsAction.RATE_APP        -> sendEffect(SettingsEffect.NavigateToRateApp)
            SettingsUiState.SettingsAction.LANGUAGE        -> openLanguageDialog()
            SettingsUiState.SettingsAction.THEME           -> openThemeDialog()
            SettingsUiState.SettingsAction.MADHAB          -> openMadhabDialog()
            SettingsUiState.SettingsAction.CALCULATION_METHOD -> openCalculationMethodDialog()
            SettingsUiState.SettingsAction.TEXT_FONT       -> openFontSizeDialog()
            SettingsUiState.SettingsAction.TAFSEER         -> openTafseerDialog()
            SettingsUiState.SettingsAction.MOAZEN_FAJR    -> openMoazenDialog(Prayer.PrayerName.FAJR)
            SettingsUiState.SettingsAction.MOAZEN_ZUHR    -> openMoazenDialog(Prayer.PrayerName.ZUHR)
            SettingsUiState.SettingsAction.MOAZEN_ASR     -> openMoazenDialog(Prayer.PrayerName.ASR)
            SettingsUiState.SettingsAction.MOAZEN_MAGHRIB -> openMoazenDialog(Prayer.PrayerName.MAGHRIB)
            SettingsUiState.SettingsAction.MOAZEN_ISHA    -> openMoazenDialog(Prayer.PrayerName.ISHA)
            else -> {}
        }
    }

    override fun onPrayerMoazenClick(prayer: Prayer.PrayerName) {
        openMoazenDialog(prayer)
    }

    private fun openMoazenDialog(prayer: Prayer.PrayerName) {
        val state = screenState.value
        val current = state.prayerMoazens[prayer] ?: SettingsUiState.Moazen.AZAN_MAKKAH
        updateState { it.copy(activeMoazenPrayer = prayer) }
        openDialog(
            type = SettingsUiState.SelectionDialogType.MOAZEN,
            titleRes = R.string.choose_moazen,
            descriptionRes = R.string.moazen_description,
            options = SettingsUiState.Moazen.entries.map { moazen ->
                SelectionItem(text = getMoazenNameRes(moazen), resId = moazenResMap[moazen])
            },
            selectedIndex = SettingsUiState.Moazen.entries.indexOf(current)
        )
    }

    // ---- Dialog confirm ----
    override fun onDialogConfirm(index: Int) {
        val dialog = screenState.value.dialog ?: return
        when (dialog.type) {
            SettingsUiState.SelectionDialogType.LANGUAGE -> {
                val selected = SettingsUiState.Language.entries[index]
                viewModelScope.launch { settingsRepository.saveLanguage(selected.toDomain()) }
                updateState { it.copy(selectedLanguage = selected) }
            }
            SettingsUiState.SelectionDialogType.THEME -> {
                val selected = SettingsUiState.ThemeState.entries[index]
                viewModelScope.launch { settingsRepository.saveTheme(selected.toDomain()) }
                updateState { it.copy(selectedTheme = selected) }
            }
            SettingsUiState.SelectionDialogType.MADHAB -> {
                val selected = SettingsUiState.MadhabState.entries[index]
                viewModelScope.launch { settingsRepository.saveMadhab(selected.toDomain()) }
                updateState { it.copy(selectedMadhab = selected) }
            }
            SettingsUiState.SelectionDialogType.CALCULATION_METHOD -> {
                val selected = SettingsUiState.CalculationMethod.entries[index]
                viewModelScope.launch { settingsRepository.saveCalculationMethod(selected.toDomain()) }
                updateState { it.copy(selectedCalculationMethod = selected) }
            }
            SettingsUiState.SelectionDialogType.MOAZEN -> {
                val selected = SettingsUiState.Moazen.entries[index]
                val prayer   = screenState.value.activeMoazenPrayer ?: return
                val newMap   = screenState.value.prayerMoazens.toMutableMap().also { it[prayer] = selected }
                updateState { it.copy(prayerMoazens = newMap, activeMoazenPrayer = null) }
                viewModelScope.launch {
                    settingsRepository.saveSelectedMoazenForPrayer(prayer, selected.fileName)
                }
            }
            SettingsUiState.SelectionDialogType.FONT_SIZE -> {
                val selected = SettingsUiState.QuranFontSize.entries[index]
                viewModelScope.launch(Dispatchers.IO) {
                    settingsRepository.saveQuranFontSize(selected.sizeSp)
                    updateState { it.copy(selectedFontSize = selected) }
                    rebuildSections()
                }
            }
            SettingsUiState.SelectionDialogType.TAFSEER -> {
                val selected = SettingsUiState.TafseerType.entries[index]
                viewModelScope.launch { settingsRepository.saveTafseer(selected.fileName) }
                updateState { it.copy(selectedTafseer = selected) }
            }
        }
        updateState { it.copy(dialog = null) }
        rebuildSections()
    }

    override fun onDialogDismiss() {
        updateState { it.copy(dialog = null, activeMoazenPrayer = null) }
    }

    // ---- helpers ----
    private fun getMoazenNameRes(moazen: SettingsUiState.Moazen): Int = when (moazen) {
        SettingsUiState.Moazen.AZAN_ABED_ALBASET      -> R.string.moazen_abed_albaset
        SettingsUiState.Moazen.AZAN_MAKKAH             -> R.string.moazen_makkah
        SettingsUiState.Moazen.AZAN_MANSOOR_AL_ZAHRANI -> R.string.moazen_mansoor
        SettingsUiState.Moazen.AZAN_MISHARY_ALAFASI    -> R.string.moazen_mishary
        SettingsUiState.Moazen.AZAN_MOHAMMED_ALMENSHWY -> R.string.moazen_menshawy
        SettingsUiState.Moazen.AZAN_NASSER_ALQATAMI    -> R.string.moazen_nasser
        SettingsUiState.Moazen.AZAN_SUHAIB_KHATBA      -> R.string.moazen_suhaib
    }

    private fun getTafseerNameRes(type: SettingsUiState.TafseerType): Int = when (type) {
        SettingsUiState.TafseerType.MOKHTASAR -> R.string.tafseer_mokhtasar
        SettingsUiState.TafseerType.MOYASSAR  -> R.string.tafseer_moyasser
    }

    private fun openDialog(type: SettingsUiState.SelectionDialogType, titleRes: Int,
        descriptionRes: Int? = null, options: List<SelectionItem>, selectedIndex: Int) {
        updateState {
            it.copy(dialog = SettingsUiState.SelectionDialogUiState(
                titleRes = titleRes, descriptionRes = descriptionRes,
                options = options, selectedIndex = selectedIndex, type = type))
        }
    }

    private fun openLanguageDialog() {
        openDialog(SettingsUiState.SelectionDialogType.LANGUAGE, R.string.choose_language,
            R.string.language_description,
            SettingsUiState.Language.entries.map { SelectionItem(it.nameRes) },
            SettingsUiState.Language.entries.indexOf(screenState.value.selectedLanguage))
    }
    private fun openThemeDialog() {
        openDialog(SettingsUiState.SelectionDialogType.THEME, R.string.choose_theme,
            R.string.theme_description,
            SettingsUiState.ThemeState.entries.map { SelectionItem(it.value) },
            SettingsUiState.ThemeState.entries.indexOf(screenState.value.selectedTheme))
    }
    private fun openMadhabDialog() {
        openDialog(SettingsUiState.SelectionDialogType.MADHAB, R.string.choose_madhab,
            R.string.madhab_description,
            SettingsUiState.MadhabState.entries.map { SelectionItem(it.value) },
            SettingsUiState.MadhabState.entries.indexOf(screenState.value.selectedMadhab))
    }
    private fun openCalculationMethodDialog() {
        openDialog(SettingsUiState.SelectionDialogType.CALCULATION_METHOD, R.string.choose_calculation_method,
            R.string.calculation_method_description,
            SettingsUiState.CalculationMethod.entries.map { SelectionItem(it.value) },
            SettingsUiState.CalculationMethod.entries.indexOf(screenState.value.selectedCalculationMethod))
    }
    private fun openFontSizeDialog() {
        openDialog(SettingsUiState.SelectionDialogType.FONT_SIZE, R.string.choose_font_size,
            R.string.font_size_description,
            SettingsUiState.QuranFontSize.entries.map { SelectionItem(it.value) },
            SettingsUiState.QuranFontSize.entries.indexOf(screenState.value.selectedFontSize))
    }
    private fun openTafseerDialog() {
        openDialog(SettingsUiState.SelectionDialogType.TAFSEER, R.string.choose_tafseer,
            R.string.tafseer_description,
            SettingsUiState.TafseerType.entries.map { SelectionItem(it.value) },
            SettingsUiState.TafseerType.entries.indexOf(screenState.value.selectedTafseer))
    }

    override fun onLocationClick()          { sendEffect(SettingsEffect.NavigateToLocation) }
    override fun onCalculationMethodClick() {}
    override fun onHelpFeedbackClick()      { sendEffect(SettingsEffect.NavigateToHelpFeedback) }
    override fun onRateAppClick()           { sendEffect(SettingsEffect.NavigateToRateApp) }
    override fun onAboutClick()             { sendEffect(SettingsEffect.NavigateToAbout) }
}
