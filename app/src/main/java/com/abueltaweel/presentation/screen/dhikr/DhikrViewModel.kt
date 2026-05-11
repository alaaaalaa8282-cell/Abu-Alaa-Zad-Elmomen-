package com.abueltaweel.presentation.screen.dhikr

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.abueltaweel.R
import com.abueltaweel.presentation.service.DhikrService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DhikrItem(val id: Int, val textAr: String, val rawResId: Int)

data class IntervalOption(val label: String, val minutes: Long)

val allDhikrs = listOf(
    DhikrItem(1, "الحمد لله",                 R.raw.alhamdo_lelah),
    DhikrItem(2, "اللهم لك الحمد",            R.raw.allahom_lk_alhamd),
    DhikrItem(3, "آية الأحزاب",               R.raw.ayah_elahzab),
    DhikrItem(4, "لا حول ولا قوة إلا بالله",  R.raw.lahawla_wlaqowat),
    DhikrItem(5, "الصلاة على النبي",           R.raw.nozaker_salt_ala_habib),
    DhikrItem(6, "ربنا اغفر لي",              R.raw.rbna_ighfer_li),
    DhikrItem(7, "سبحان الله وبحمده",          R.raw.sobhanallah_wabehamdeh)
)

val intervalOptions = listOf(
    IntervalOption("1 دقيقة",   1L),
    IntervalOption("5 دقائق",   5L),
    IntervalOption("10 دقائق",  10L),
    IntervalOption("30 دقيقة",  30L),
    IntervalOption("ساعة",      60L),
    IntervalOption("ساعتين",    120L),
    IntervalOption("3 ساعات",   180L)
)

data class DhikrUiState(
    val selectedInterval: IntervalOption = intervalOptions[1],
    val volume: Float = 0.8f,
    val isRunning: Boolean = false,
    val currentDhikrIndex: Int = 0
)

class DhikrViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DhikrUiState())
    val uiState: StateFlow<DhikrUiState> = _uiState.asStateFlow()

    fun selectInterval(option: IntervalOption) {
        _uiState.value = _uiState.value.copy(selectedInterval = option)
    }

    fun setVolume(v: Float) {
        _uiState.value = _uiState.value.copy(volume = v)
    }

    fun start(context: Context) {
        val state = _uiState.value
        val intent = Intent(context, DhikrService::class.java).apply {
            putExtra(DhikrService.EXTRA_RES_IDS,
                allDhikrs.map { it.rawResId }.toIntArray())
            putExtra(DhikrService.EXTRA_TEXTS,
                allDhikrs.map { it.textAr }.toTypedArray())
            putExtra(DhikrService.EXTRA_INTERVAL_MS,
                state.selectedInterval.minutes * 60 * 1000L)
            putExtra(DhikrService.EXTRA_VOLUME, state.volume)
        }
        context.startForegroundService(intent)
        _uiState.value = state.copy(isRunning = true, currentDhikrIndex = 0)
    }

    fun stop(context: Context) {
        try {
            context.startService(
                Intent(context, DhikrService::class.java).apply {
                    action = DhikrService.ACTION_STOP
                }
            )
        } catch (_: Exception) {}
        _uiState.value = _uiState.value.copy(isRunning = false)
    }
}
