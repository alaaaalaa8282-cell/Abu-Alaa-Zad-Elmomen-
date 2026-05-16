package com.abueltaweel.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.abueltaweel.R
import com.abueltaweel.domain.entity.location.Location
import com.abueltaweel.domain.repository.prayer.PrayerRepository
import com.abueltaweel.domain.repository.settings.SettingsRepository
import com.abueltaweel.presentation.base.MainActivity
import com.abueltaweel.presentation.utils.convertMillisToHMS
import com.abueltaweel.presentation.utils.getTimeDifference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.ext.android.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PrayerCountdownService : Service() {

    private val settingsRepository: SettingsRepository by inject()
    private val prayerRepository: PrayerRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var countdownJob: Job? = null

    companion object {
        const val CHANNEL_ID = "prayer_countdown_channel"
        const val NOTIF_ID   = 55
        const val ACTION_STOP = "STOP_COUNTDOWN"
        var isRunning = false
    }

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        isRunning = true
        createChannel()
        startForeground(NOTIF_ID, buildNotification("جارٍ الحساب...", ""))
        startCountdown()
        return START_STICKY
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = scope.launch {
            while (true) {
                try {
                    val settings = settingsRepository.observeAppSettings().first().prayerSettings
                    val now = Clock.System.now()
                    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

                    val nextPrayer = prayerRepository.getNextPrayer(
                        instant = now,
                        madhab = settings.madhab,
                        calculationMethod = settings.calculationMethod,
                        location = Location(
                            longitude = settings.location.longitude,
                            latitude = settings.location.latitude,
                            country = "",
                            state = ""
                        ),
                        date = today
                    )

                    val nextMillis = nextPrayer.time.toEpochMilliseconds()
                    val arabicName = nextPrayer.name.getArabicName()

                    // عداد ثانية بثانية
                    while (true) {
                        val diff = getTimeDifference(nextMillis)
                        if (diff <= 0) break

                        val time = convertMillisToHMS(diff)
                        val timeStr = "${time.first}:${time.second}:${time.third}"
                        updateNotification(arabicName, timeStr)
                        delay(1000)
                    }
                    // لما الوقت يخلص، احسب الصلاة الجاية
                    delay(2000)

                } catch (e: Exception) {
                    delay(5000)
                }
            }
        }
    }

    private fun updateNotification(prayerName: String, timeLeft: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIF_ID, buildNotification(prayerName, timeLeft))
    }

    private fun buildNotification(prayerName: String, timeLeft: String): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = if (prayerName.isEmpty()) "زاد المؤمن" else "باقي على $prayerName"
        val content = if (timeLeft.isEmpty()) "جارٍ الحساب..." else timeLeft

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.mosque_02)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)
           .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) 
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID, "عداد الصلاة",
                        NotificationManager.IMPORTANCE_DEFAULT 
                    ).apply {
                        setSound(null, null)
                        enableVibration(false)
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        isRunning = false
        countdownJob?.cancel()
        super.onDestroy()
    }
}
