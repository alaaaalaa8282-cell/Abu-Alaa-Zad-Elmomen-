package com.abueltaweel.presentation.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.abueltaweel.R
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.domain.repository.settings.SettingsRepository
import com.abueltaweel.presentation.base.MainActivity
import com.abueltaweel.presentation.screen.azan.AzanFullScreenActivity
import com.abueltaweel.presentation.utils.Constants
import com.abueltaweel.presentation.utils.Constants.AZAN_CHANNEL_ID
import com.abueltaweel.presentation.utils.Constants.AZAN_CHANNEL_NAME
import com.abueltaweel.presentation.utils.Constants.PRAYER_NAME_KEY
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class PrayerAlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val settingsRepository: SettingsRepository by inject()

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == Constants.ACTION_STOP_AZAN) {
            stopAzan()
            return START_STICKY
        }
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) return START_NOT_STICKY

        createChannel()

        val prayerNameStr = intent?.getStringExtra(PRAYER_NAME_KEY) ?: return START_NOT_STICKY
        val prayerEnum = runCatching {
            Prayer.PrayerName.valueOf(prayerNameStr)
        }.getOrDefault(Prayer.PrayerName.FAJR)

        startForeground(1, createNotification(prayerEnum))

        // إطلاق شاشة الأذان الـ Full Screen
        startActivity(AzanFullScreenActivity.newIntent(this, prayerEnum.getArabicName()))

        playAzanForPrayer(prayerEnum)

        return START_NOT_STICKY
    }

    private fun stopAzan() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) mediaPlayer.stop()
        stopForeground(true)
        stopSelf()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AZAN_CHANNEL_ID, AZAN_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null)
                enableVibration(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun createNotification(prayer: Prayer.PrayerName): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, PrayerAlarmService::class.java).apply {
                action = Constants.ACTION_STOP_AZAN
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, AZAN_CHANNEL_ID)
            .setFullScreenIntent(openIntent, true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentTitle("أذان ${prayer.getArabicName()}")
            .setContentText("اضغط هنا لرؤية مواقيت الصلاة")
            .setSmallIcon(R.drawable.mosque_02)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(R.drawable.ic_close_circle, "إيقاف", stopIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun playAzanForPrayer(prayer: Prayer.PrayerName) {
        // قراءة المؤذن المختار لهذه الصلاة تحديداً
        val selectedFileName = runBlocking {
            settingsRepository.observeSelectedMoazenForPrayer(prayer).first()
        }.removeSuffix(".mp3")

        val resId = resources.getIdentifier(selectedFileName, "raw", packageName)
        val audioResId = if (resId != 0) resId else R.raw.azan_makkah

        val afd = resources.openRawResourceFd(audioResId)
        mediaPlayer = MediaPlayer().apply {
            setWakeMode(this@PrayerAlarmService, PowerManager.PARTIAL_WAKE_LOCK)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
            setOnCompletionListener {
                stopForeground(true)
                stopSelf()
            }
        }
        afd.close()
    }

    override fun onDestroy() {
        if (::mediaPlayer.isInitialized) mediaPlayer.release()
        super.onDestroy()
    }
}

fun Prayer.PrayerName.getArabicName() = when (this) {
    Prayer.PrayerName.FAJR    -> "الفجر"
    Prayer.PrayerName.ZUHR    -> "الظهر"
    Prayer.PrayerName.ASR     -> "العصر"
    Prayer.PrayerName.MAGHRIB -> "المغرب"
    Prayer.PrayerName.ISHA    -> "العشاء"
}
