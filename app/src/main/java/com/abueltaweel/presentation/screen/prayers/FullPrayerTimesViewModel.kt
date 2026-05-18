```kotlin
@file:OptIn(ExperimentalTime::class)

package com.abueltaweel.presentation.screen.prayers

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.abueltaweel.R
import com.abueltaweel.domain.entity.location.Location
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.domain.model.RescheduleResult
import com.abueltaweel.domain.repository.prayer.PrayerNotificationsRepository
import com.abueltaweel.domain.repository.prayer.PrayerRepository
import com.abueltaweel.domain.repository.settings.SettingsRepository
import com.abueltaweel.domain.usecase.PrayerSchedulingUseCase
import com.abueltaweel.presentation.base.BaseViewModel
import com.abueltaweel.presentation.utils.convertMillisToHMS
import com.abueltaweel.presentation.utils.getTimeDifference
import com.abueltaweel.presentation.utils.isIgnoringBatteryOptimizations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FullPrayerTimesViewModel(
    private val prayerRepository: PrayerRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationsRepository: PrayerNotificationsRepository,
    private val prayerSchedulingUseCase: PrayerSchedulingUseCase,
    private val context: Context
) : BaseViewModel<FullPrayerTimesUiState, FullPrayerTimesEffect>(FullPrayerTimesUiState()),
    FullPrayerTimeInteractionListener {
    private var countdownJob: Job? = null
    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private var exactAlarmRequested = false
    private var batteryOptRequested = false
    private var autoStartRequested = false
    private val isXiaomiDevice: Boolean
        get() = android.os.Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)
    private val _countdownTime =
        MutableStateFlow(FullPrayerTimesUiState.TimeUiState("00", "00", "00"))
    val countdownTime: StateFlow<FullPrayerTimesUiState.TimeUiState> = _countdownTime.asStateFlow()

    init {
        observePrayerNotifications()
        getDailyPrayers()
        scheduleAlarmsIfNeeded()
    }

    private fun scheduleAlarmsIfNeeded() {
        viewModelScope.launch {
            combine(
                settingsRepository.observePrayerSettings().distinctUntilChanged(),
                notificationsRepository.observeAll().distinctUntilChanged()
            ) { settings, notifications ->
                settings to notifications
            }
                .collect {
                    val result = prayerSchedulingUseCase.rescheduleTodayPrayerAlarms()
                    Log.d("AZAN_DEBUG", "Reschedule triggered $result")

                    if (result == RescheduleResult.PermissionRequired && !exactAlarmRequested) {
                        exactAlarmRequested = true
                        sendEffect(FullPrayerTimesEffect.RequestExactAlarm)
                    }
                }
        }
    }

    fun onScreenOpened() {
        // يمكن إضافة أي منطق عند فتح الشاشة هنا
    }

    private fun getDailyPrayers() {
        tryToCall(
            block = ::getDailyPrayersBlock,
            onSuccess = ::onGetDailyPrayersSuccess,
            onError = {}
        )
    }

    private suspend fun getDailyPrayersBlock(): List<FullPrayerTimesUiState.PrayerUiState> {
        val settings = settingsRepository.observeAppSettings().first().prayerSettings
        val prayers = prayerRepository.getDailyPrayers(
            madhab = settings.madhab,
            calculationMethod = settings.calculationMethod,
            location = Location(
                longitude = settings.location.longitude,
                latitude = settings.location.latitude,
            ),
            date = today
        )
        val zone = TimeZone.currentSystemDefault()
        return prayers.map { it.toPrayerUiState(zone = zone) }
    }

    private fun onGetDailyPrayersSuccess(prayers: List<FullPrayerTimesUiState.PrayerUiState>) {
        val now = Clock.System.now().toEpochMilliseconds()
        val updatedPrayers = calculatePrayerProgress(prayers, now)

        updateState { currentState ->
            currentState.copy(prayers = updatedPrayers)
        }
        getNextPrayer()
    }

    private fun getNextPrayer() {
        tryToCall(
            block = ::getNextPrayerBlock,
            onSuccess = ::onGetNextPrayerSuccess,
            onError = {
                updateState { currentState ->
                    currentState.copy(nextPrayer = FullPrayerTimesUiState.PrayerUiState())
                }
            }
        )
    }

    private suspend fun getNextPrayerBlock(): Prayer {
        val settings = settingsRepository.observeAppSettings().first().prayerSettings
        val nextPrayer = prayerRepository.getNextPrayer(
            instant = Clock.System.now(),
            madhab = settings.madhab,
            calculationMethod = settings.calculationMethod,
            location = Location(
                longitude = settings.location.longitude,
                latitude = settings.location.latitude,
            ),
            date = today
        )
        return nextPrayer
    }

    private fun onGetNextPrayerSuccess(prayer: Prayer) {
        val zone = TimeZone.currentSystemDefault()
        val nextUi = prayer.toPrayerUiState(zone).copy(isUpComing = true)

        updateState { current ->
            val updatedList = current.prayers.map {
                it.copy(isUpComing = (it.name == nextUi.name))
            }

            current.copy(
                prayers = updatedList,
                nextPrayer = nextUi
            )
        }
        val nextInstantMillis = prayer.time.toEpochMilliseconds()
        startCountdown(nextInstantMillis)
    }

    private fun startCountdown(nextPrayerMillis: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val diff = getTimeDifference(nextPrayerMillis)
                if (diff <= 0) {
                    _countdownTime.value = FullPrayerTimesUiState.TimeUiState("00", "00", "00")
                    getNextPrayer()
                    break
                }
                val time = convertMillisToHMS(diff)
                _countdownTime.value = FullPrayerTimesUiState.TimeUiState(
                    hours = time.first,
                    minutes = time.second,
                    seconds = time.third
                )
                delay(1000)
            }
        }
    }

    private fun updateCountdownUi(time: Triple<String, String, String>) {
        updateState { current ->
            current.copy(
                time = FullPrayerTimesUiState.TimeUiState(
                    hours = time.first,
                    minutes = time.second,
                    seconds = time.third
                )
            )
        }
    }

    private fun handleCountdownFinished() {
        updateState { current ->
            current.copy(
                time = FullPrayerTimesUiState.TimeUiState("00", "00", "00")
            )
        }
    }

    private fun observePrayerNotifications() {
        viewModelScope.launch {
            notificationsRepository.observeAll().collect { map ->
                updateState { current ->
                    val updatedPrayers = current.prayers.map { prayer ->
                        // هنا نستخدم prayer.name مباشرة لأنه Enum ولا يحتاج استدعاء لـ toPrayerName()
                        val prayerName = prayer.name 
                        prayer.copy(
                            isNotificationEnabled = map[prayerName] ?: false
                        )
                    }

                    current.copy(
                        prayers = updatedPrayers
                    )
                }
            }
        }
    }

    fun Prayer.PrayerName.toStringRes(): Int = when (this) {
        Prayer.PrayerName.FAJR -> R.string.fajr
        Prayer.PrayerName.ZUHR -> R.string.dhuhr
        Prayer.PrayerName.ASR -> R.string.asr
        Prayer.PrayerName.MAGHRIB -> R.string.maghrib
        Prayer.PrayerName.ISHA -> R.string.isha
    }

    fun calculatePrayerProgress(
        prayers: List<FullPrayerTimesUiState.PrayerUiState>,
        nowMillis: Long
    ): List<FullPrayerTimesUiState.PrayerUiState> {
        val todayStartMillis = prayers.firstOrNull()?.instantTime
            ?.toLocalDateTime(TimeZone.currentSystemDefault())
            ?.date
            ?.atStartOfDayIn(TimeZone.currentSystemDefault())
            ?.toEpochMilliseconds() ?: 0L

        return prayers.mapIndexed { index, prayer ->
            val prayerTimeMillis = prayer.instantTime?.toEpochMilliseconds() ?: 0L
            val previousPrayerTimeMillisCorrected = if (index > 0) {
                prayers[index - 1].instantTime?.toEpochMilliseconds() ?: 0L
            } else {
                todayStartMillis
            }

            val progress = when {
                nowMillis >= prayerTimeMillis -> 1f
                nowMillis < previousPrayerTimeMillisCorrected -> 0f
                else -> {
                    val elapsed = nowMillis - previousPrayerTimeMillisCorrected
                    val duration = prayerTimeMillis - previousPrayerTimeMillisCorrected
                    (elapsed.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                }
            }

            prayer.copy(progress = progress)
        }
    }

    override fun onClickBack() {
        sendEffect(FullPrayerTimesEffect.NavigateBack)
    }

    override fun onClickEnablePrayer(
        prayerName: Prayer.PrayerName,
        isEnabled: Boolean
    ) {
        if (isEnabled) {
            checkPermissionsBeforeEnable()
        }
        tryToCall(
            block = {
                notificationsRepository.setPrayerEnabled(
                    prayer = prayerName,
                    enabled = isEnabled
                )
            },
            onSuccess = {
                // تحديث حالة التنبيهات محلياً فوراً لتحسين تجربة المستخدم وسلاسة الواجهة
                updateState { current ->
                    val updatedPrayers = current.prayers.map { prayer ->
                        if (prayer.name == prayerName) {
                            prayer.copy(isNotificationEnabled = isEnabled)
                        } else {
                            prayer
                        }
                    }
                    current.copy(prayers = updatedPrayers)
                }
            },
            onError = {}
        )
    }

    private fun checkPermissionsBeforeEnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            sendEffect(FullPrayerTimesEffect.RequestNotificationPermission)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                sendEffect(FullPrayerTimesEffect.RequestExactAlarm)
                return
            }
        }
        if (!isIgnoringBatteryOptimizations(context)) {
            sendEffect(FullPrayerTimesEffect.ShowBatteryOptimizationDialog)
            return
        }
        if (isXiaomiDevice) {
            sendEffect(FullPrayerTimesEffect.RequestXiaomiAutoStart)
        }
    }
}

```

