package com.abueltaweel.presentation.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
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
    private val audioManager by lazy { getSystemService(AUDIO_SERVICE) as android.media.AudioManager }
 
    // channel مخصوص للـ foreground بدون ظهور
    private val SILENT_CHANNEL_ID   = "azan_silent_fg"
    private val SILENT_CHANNEL_NAME = "أذان (خلفية)"

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == Constants.ACTION_STOP_AZAN) {
            stopAzan()
            isPlaying = false
            return START_STICKY
        }
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) return START_NOT_STICKY

        createChannels()

        val prayerNameStr = intent?.getStringExtra(PRAYER_NAME_KEY) ?: return START_NOT_STICKY
        val prayerEnum = runCatching {
            Prayer.PrayerName.valueOf(prayerNameStr)
        }.getOrDefault(Prayer.PrayerName.FAJR)

        // ← الإشعار الصامت المخفي فقط عشان startForeground يشتغل
        startForeground(1, createSilentNotification())
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val req = android.media.AudioFocusRequest.Builder(android.media.AudioManager.AUDIOFOCUS_GAIN)
.setOnAudioFocusChangeListener { focusChange ->
    if (focusChange == android.media.AudioManager.AUDIOFOCUS_LOSS ||
    focusChange == android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
    mediaPlayer.pause()
} else if (focusChange == android.media.AudioManager.AUDIOFOCUS_GAIN) {
    mediaPlayer.start()
}
}
        .build()
    audioManager.requestAudioFocus(req)
} else {
    @Suppress("DEPRECATION")
    audioManager.requestAudioFocus(null, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN)
       }
        isPlaying = true         // فتح شاشة الأذان
        if (canShowOverlay()) {
            startActivity(AzanFullScreenActivity.newIntent(this, prayerEnum.getArabicName()))
        } else {
            requestOverlayPermission()
        }

        playAzanForPrayer(prayerEnum)

        return START_NOT_STICKY
    }

    private fun canShowOverlay(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
   
    }

    private fun stopAzan() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) mediaPlayer.stop()
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    audioManager.abandonAudioFocusRequest(
        android.media.AudioFocusRequest.Builder(android.media.AudioManager.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener {}
            .build()
    )
       }
        stopForeground(true)
        stopSelf()
    }
companion object {
    @Volatile var isPlaying: Boolean = false
}
    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)

            // channel الأصلي للأذان (نحتفظ بيه عشان مش هنحذفه)
            if (nm.getNotificationChannel(AZAN_CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        AZAN_CHANNEL_ID, AZAN_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        setSound(null, null)
                        enableVibration(false)
                    }
                )
            }

            // ← channel صامت ومخفي تماماً للـ foreground
            if (nm.getNotificationChannel(SILENT_CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        SILENT_CHANNEL_ID, SILENT_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_MIN   // ← مخفي من الـ status bar
                    ).apply {
                        setSound(null, null)
                        enableVibration(false)
                        setShowBadge(false)
                    }
                )
            }
        }
    }

    // ← إشعار صامت ومخفي - فقط عشان startForeground
    private fun createSilentNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, SILENT_CHANNEL_ID)
            .setContentTitle("أذان")
            .setContentText("")
            .setSmallIcon(R.drawable.mosque_02)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)   // ← أدنى أولوية
            .setSilent(true)                                // ← صامت
            .setVisibility(NotificationCompat.VISIBILITY_SECRET) // ← مخفي من شاشة القفل
            .build()
    }

    private fun playAzanForPrayer(prayer: Prayer.PrayerName) {
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
    sendBroadcast(Intent(Constants.ACTION_STOP_AZAN))
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
