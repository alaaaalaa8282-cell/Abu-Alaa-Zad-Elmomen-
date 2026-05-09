package com.abueltaweel.presentation.screen.dhikr

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
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
    val count: Int = 33,
    val intervalSec: Int = 3,
    val isRunning: Boolean = false
)

val allDhikrs = listOf(
    DhikrItem(1,  "الحمد لله",                     R.raw.alhamdo_lelah),
    DhikrItem(2,  "اللهم لك الحمد",                R.raw.allahom_lk_alhamd),
    DhikrItem(3,  "آية الأحزاب",                   R.raw.ayah_elahzab),
    DhikrItem(4,  "لا حول ولا قوة إلا بالله",      R.raw.lahawla_wlaqowat),
    DhikrItem(5,  "الصلاة على النبي",               R.raw.nozaker_salt_ala_habib),
    DhikrItem(6,  "ربنا اغفر لي",                  R.raw.rbna_ighfer_li),
    DhikrItem(7,  "سبحان الله وبحمده",              R.raw.sobhanallah_wabehamdeh)
)

class DhikrViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DhikrUiState())
    val uiState: StateFlow<DhikrUiState> = _uiState.asStateFlow()

    fun selectDhikr(item: DhikrItem) {
        _uiState.value = _uiState.value.copy(selectedDhikr = item)
    }

    fun setCount(count: Int) {
        _uiState.value = _uiState.value.copy(count = count.coerceIn(1, 1000))
    }

    fun setInterval(sec: Int) {
        _uiState.value = _uiState.value.copy(intervalSec = sec.coerceIn(1, 60))
    }

    fun start(context: Context) {
        val state = _uiState.value
        context.startForegroundService(
            Intent(context, DhikrService::class.java).apply {
                putExtra(DhikrService.EXTRA_TEXT,        state.selectedDhikr.textAr)
                putExtra(DhikrService.EXTRA_RES_ID,      state.selectedDhikr.rawResId)
                putExtra(DhikrService.EXTRA_COUNT,       state.count)
                putExtra(DhikrService.EXTRA_INTERVAL_MS, state.intervalSec * 1000L)
            }
        )
        _uiState.value = state.copy(isRunning = true)
    }

    fun stop(context: Context) {
        context.startService(
            Intent(context, DhikrService::class.java).apply { action = DhikrService.ACTION_STOP }
        )
        _uiState.value = _uiState.value.copy(isRunning = false)
    }
}
