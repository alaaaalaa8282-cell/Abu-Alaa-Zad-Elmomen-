package com.abueltaweel.presentation.screen.dhikr

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.abueltaweel.R
import com.abueltaweel.presentation.service.DhikrService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class DhikrItem(
    val id: Int,
    val textAr: String,
    val rawResId: Int
)

data class DhikrUiState(
    val dhikrList: List<DhikrItem> = allDhikrs,
    val selectedDhikr: DhikrItem = allDhikrs.first(),
    val intervalMin: Int = 5,
    val volume: Float = 0.8f,
    val isRunning: Boolean = false,
    val repeatSingle: Boolean = false,
    // ─── وقت التشغيل التلقائي ───
    val autoEnabled: Boolean = false,
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val stopHour: Int = 22,
    val stopMinute: Int = 0
)

val allDhikrs = listOf(
    DhikrItem(1, "الحمد لله",                 R.raw.alhamdo_lelah),
    DhikrItem(2, "اللهم لك الحمد",            R.raw.allahom_lk_alhamd),
    DhikrItem(3, "آية الأحزاب",               R.raw.ayah_elahzab),
    DhikrItem(4, "لا حول ولا قوة إلا بالله",  R.raw.lahawla_wlaqowat),
    DhikrItem(5, "الصلاة على النبي",           R.raw.nozaker_salt_ala_habib),
    DhikrItem(6, "ربنا اغفر لي",              R.raw.rbna_ighfer_li),
    DhikrItem(7, "سبحان الله وبحمده",          R.raw.sobhanallah_wabehamdeh), 
    DhikrItem(8, "الله أكبر",          R.raw.allah_akbar),
    DhikrItem(9, "أستغفر الله",        R.raw.astaghfer_allah),
    DhikrItem(10, "لا إله إلا الله",   R.raw.la_ilah_ela_allah),
)

private const val PREFS_NAME      = "dhikr_prefs"
private const val KEY_DHIKR_ID   = "dhikr_id"
private const val KEY_INTERVAL   = "dhikr_interval"
private const val KEY_VOLUME     = "dhikr_volume"
private const val KEY_AUTO       = "dhikr_auto"
private const val KEY_START_H    = "dhikr_start_h"
private const val KEY_START_M    = "dhikr_start_m"
private const val KEY_STOP_H     = "dhikr_stop_h"
private const val KEY_STOP_M     = "dhikr_stop_m"

private const val REQ_START = 1001
private const val REQ_STOP  = 1002

class DhikrViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        DhikrUiState(
            selectedDhikr = allDhikrs.firstOrNull { it.id == prefs.getInt(KEY_DHIKR_ID, 1) } ?: allDhikrs.first(),
            intervalMin   = prefs.getInt(KEY_INTERVAL, 5),
            volume        = prefs.getFloat(KEY_VOLUME, 0.8f),
            isRunning     = DhikrService.isRunning,
            autoEnabled   = prefs.getBoolean(KEY_AUTO, false),
            startHour     = prefs.getInt(KEY_START_H, 8),
            startMinute   = prefs.getInt(KEY_START_M, 0),
            stopHour      = prefs.getInt(KEY_STOP_H, 22),
                stopMinute    = prefs.getInt(KEY_STOP_M, 0),
        repeatSingle  = prefs.getBoolean("dhikr_repeat_single", false)
    )
)
    val uiState: StateFlow<DhikrUiState> = _uiState.asStateFlow()

    fun selectDhikr(item: DhikrItem) {
        prefs.edit().putInt(KEY_DHIKR_ID, item.id).apply()
        _uiState.value = _uiState.value.copy(selectedDhikr = item)
    }
fun setRepeatSingle(repeat: Boolean) {
    prefs.edit().putBoolean("dhikr_repeat_single", repeat).apply()
    _uiState.value = _uiState.value.copy(repeatSingle = repeat)
}
    
    fun setInterval(minutes: Int) {
        val v = minutes.coerceIn(1, 60)
        prefs.edit().putInt(KEY_INTERVAL, v).apply()
        _uiState.value = _uiState.value.copy(intervalMin = v)
    }

    fun setVolume(v: Float, context: Context) {
    val clamped = v.coerceIn(0f, 1f)
    prefs.edit().putFloat(KEY_VOLUME, clamped).apply()
    _uiState.value = _uiState.value.copy(volume = clamped)
    if (DhikrService.isRunning) {
        context.startService(
            Intent(context, DhikrService::class.java).apply {
                action = DhikrService.ACTION_UPDATE_VOLUME
                putExtra(DhikrService.EXTRA_VOLUME, clamped)
            }
        )
    }
}
    fun setAutoEnabled(enabled: Boolean, context: Context) {
        prefs.edit().putBoolean(KEY_AUTO, enabled).apply()
        _uiState.value = _uiState.value.copy(autoEnabled = enabled)
        if (enabled) scheduleAlarms(context) else cancelAlarms(context)
    }

    fun setStartTime(hour: Int, minute: Int, context: Context) {
        prefs.edit().putInt(KEY_START_H, hour).putInt(KEY_START_M, minute).apply()
        _uiState.value = _uiState.value.copy(startHour = hour, startMinute = minute)
        if (_uiState.value.autoEnabled) scheduleAlarms(context)
    }

    fun setStopTime(hour: Int, minute: Int, context: Context) {
        prefs.edit().putInt(KEY_STOP_H, hour).putInt(KEY_STOP_M, minute).apply()
        _uiState.value = _uiState.value.copy(stopHour = hour, stopMinute = minute)
        if (_uiState.value.autoEnabled) scheduleAlarms(context)
    }

    fun syncServiceState(context: Context) {
        val running = DhikrService.isRunning
        if (_uiState.value.isRunning != running) {
            _uiState.value = _uiState.value.copy(isRunning = running)
        }
    }

    fun start(context: Context) {
        val state = _uiState.value
        context.startForegroundService(
            Intent(context, DhikrService::class.java).apply {
                val resIds = if (state.repeatSingle) intArrayOf(state.selectedDhikr.rawResId) else allDhikrs.map { it.rawResId }.toIntArray()
                 val texts = if (state.repeatSingle) arrayOf(state.selectedDhikr.textAr) else allDhikrs.map { it.textAr }.toTypedArray()
                putExtra(DhikrService.EXTRA_TEXTS, texts)
                putExtra(DhikrService.EXTRA_RES_IDS, resIds)
                putExtra(DhikrService.EXTRA_VOLUME,      _uiState.value.volume)
                putExtra(DhikrService.EXTRA_INTERVAL_MINUTES, _uiState.value.intervalMin)
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

    // ─── جدولة الأذكار ───
    private fun scheduleAlarms(context: Context) {
        val state = _uiState.value
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Start Intent
        val startIntent = Intent(context, DhikrService::class.java).apply {
            putExtra(DhikrService.EXTRA_RES_IDS,          allDhikrs.map { it.rawResId }.toIntArray())
            putExtra(DhikrService.EXTRA_TEXTS,            allDhikrs.map { it.textAr }.toTypedArray())
            putExtra(DhikrService.EXTRA_INTERVAL_MINUTES, _uiState.value.intervalMin)
            putExtra(DhikrService.EXTRA_VOLUME,           _uiState.value.volume)
        }
        val startPi = PendingIntent.getForegroundService(
            context, REQ_START, startIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Stop Intent
        val stopIntent = Intent(context, DhikrService::class.java).apply {
            action = DhikrService.ACTION_STOP
        }
        val stopPi = PendingIntent.getService(
            context, REQ_STOP, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // وقت البداية
        val startCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, state.startHour)
            set(Calendar.MINUTE, state.startMinute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis())
                add(Calendar.DAY_OF_YEAR, 1)
        }

        // وقت النهاية
        val stopCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, state.stopHour)
            set(Calendar.MINUTE, state.stopMinute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis())
                add(Calendar.DAY_OF_YEAR, 1)
        }

        am.setRepeating(AlarmManager.RTC_WAKEUP, startCal.timeInMillis, AlarmManager.INTERVAL_DAY, startPi)
        am.setRepeating(AlarmManager.RTC_WAKEUP, stopCal.timeInMillis,  AlarmManager.INTERVAL_DAY, stopPi)
    }

    private fun cancelAlarms(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startPi = PendingIntent.getForegroundService(
            context, REQ_START,
            Intent(context, DhikrService::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        val stopPi = PendingIntent.getService(
            context, REQ_STOP,
            Intent(context, DhikrService::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        startPi?.let { am.cancel(it) }
        stopPi?.let  { am.cancel(it) }
    }
}
