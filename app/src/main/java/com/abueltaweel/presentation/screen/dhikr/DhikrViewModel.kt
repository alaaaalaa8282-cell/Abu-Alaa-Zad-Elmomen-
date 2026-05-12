package com.abueltaweel.presentation.screen.dhikr

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.abueltaweel.R
import com.abueltaweel.presentation.service.DhikrService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DhikrItem(
    val id: Int,
    val textAr: String,
    val rawResId: Int
)

data class DhikrUiState(
    val dhikrList: List<DhikrItem> = allDhikrs,
    val selectedDhikr: DhikrItem = allDhikrs.first(),
    val intervalSec: Int = 60,
    val volume: Float = 0.8f,
    val isRunning: Boolean = false
)

val allDhikrs = listOf(
    DhikrItem(1, "الحمد لله",                 R.raw.alhamdo_lelah),
    DhikrItem(2, "اللهم لك الحمد",            R.raw.allahom_lk_alhamd),
    DhikrItem(3, "آية الأحزاب",               R.raw.ayah_elahzab),
    DhikrItem(4, "لا حول ولا قوة إلا بالله",  R.raw.lahawla_wlaqowat),
    DhikrItem(5, "الصلاة على النبي",           R.raw.nozaker_salt_ala_habib),
    DhikrItem(6, "ربنا اغفر لي",              R.raw.rbna_ighfer_li),
    DhikrItem(7, "سبحان الله وبحمده",          R.raw.sobhanallah_wabehamdeh)
)

private const val PREFS_NAME    = "dhikr_prefs"
private const val KEY_DHIKR_ID  = "dhikr_id"
private const val KEY_INTERVAL  = "dhikr_interval"
private const val KEY_VOLUME    = "dhikr_volume"

class DhikrViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val savedDhikrId  = prefs.getInt(KEY_DHIKR_ID, 1)
    private val savedInterval = prefs.getInt(KEY_INTERVAL, 60)
    private val savedVolume   = prefs.getFloat(KEY_VOLUME, 0.8f)

    private val _uiState = MutableStateFlow(
        DhikrUiState(
            selectedDhikr = allDhikrs.firstOrNull { it.id == savedDhikrId } ?: allDhikrs.first(),
            intervalSec   = savedInterval,
            volume        = savedVolume,
            isRunning     = DhikrService.isRunning
        )
    )
    val uiState: StateFlow<DhikrUiState> = _uiState.asStateFlow()

    fun selectDhikr(item: DhikrItem) {
        prefs.edit().putInt(KEY_DHIKR_ID, item.id).apply()
        _uiState.value = _uiState.value.copy(selectedDhikr = item)
    }

    fun setInterval(sec: Int) {
        val v = sec.coerceIn(10, 3600)
        prefs.edit().putInt(KEY_INTERVAL, v).apply()
        _uiState.value = _uiState.value.copy(intervalSec = v)
    }

    fun setVolume(v: Float) {
        val clamped = v.coerceIn(0f, 1f)
        prefs.edit().putFloat(KEY_VOLUME, clamped).apply()
        _uiState.value = _uiState.value.copy(volume = clamped)
    }

    fun syncServiceState(context: Context) {
        val running = DhikrService.isRunning
        if (_uiState.value.isRunning != running) {
            _uiState.value = _uiState.value.copy(isRunning = running)
        }
    }

    fun start(context: Context) {
        val state = _uiState.value
        val startIndex = allDhikrs.indexOfFirst { it.id == state.selectedDhikr.id }.coerceAtLeast(0)
        val ordered    = allDhikrs.drop(startIndex) + allDhikrs.take(startIndex)
       val resIds     = ordered.map { it.rawResId }.toIntArray()
       val texts      = ordered.map { it.textAr }.toTypedArray()

        context.startForegroundService(
            Intent(context, DhikrService::class.java).apply {
                putExtra(DhikrService.EXTRA_TEXTS,       texts)
                putExtra(DhikrService.EXTRA_RES_IDS,     resIds)
                putExtra(DhikrService.EXTRA_INTERVAL_MS, state.intervalSec * 1000L)
                putExtra(DhikrService.EXTRA_VOLUME,      state.volume)
            }
        )
        _uiState.value = state.copy(isRunning = true)
    }

    fun stop(context: Context) {
        context.startService(
            Intent(context, DhikrService::class.java).apply {
                action = DhikrService.ACTION_STOP
            }
        )
        _uiState.value = _uiState.value.copy(isRunning = false)
    }
}
