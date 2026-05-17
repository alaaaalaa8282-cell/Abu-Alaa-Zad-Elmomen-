package com.abueltaweel.presentation.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
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
    private val audioManager by lazy { getSystemService(AUDIO_SERVICE) as AudioManager }

    private val SILENT_CHANNEL_ID   = "azan_silent_fg"
    private val SILENT_CHANNEL_NAME = "أذان (خلفية)"

    private var audioFocusRequest: AudioFocusRequest? = null

    companion object {
        @Volatile var isPlaying: Boolean = false
    }

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
        val prayerEnum = Prayer.PrayerName.entries.firstOrNull {
            it.getArabicName() == prayerNameStr
        } ?: Prayer.PrayerName.FAJR

        // FIX: ارفع isPlaying أول حاجة عشان نتجنب الـ race condition
        isPlaying = true

        startForeground(1, createSilentNotification())

        // FIX: اتشالت startActivity — الشاشة بتتفتح من AzanAlarmReceiver بس
        requestAudioFocus()

        if (DhikrService.isRunning) {
            startService(Intent(this, DhikrService::class.java).apply {
                action = DhikrService.ACTION_PAUSE_FOR_AZAN
            })
        }

        playAzanForPrayer(prayerEnum)

        return START_NOT_STICKY
    }

    // ── Audio Focus ───────────────────────────────────────────────────────

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener {}
                .build()
            audioManager.requestAudioFocus(audioFocusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    // ── Stop ──────────────────────────────────────────────────────────────

    private fun stopAzan() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) mediaPlayer.stop()
        abandonAudioFocus()
        stopForeground(true)
        stopSelf()
    }

    // ── Channels ──────────────────────────────────────────────────────────

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)

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

            if (nm.getNotificationChannel(SILENT_CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        SILENT_CHANNEL_ID, SILENT_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_MIN
                    ).apply {
                        setSound(null, null)
                        enableVibration(false)
                        setShowBadge(false)
                    }
                )
            }
        }
    }

    // ── Notifications ─────────────────────────────────────────────────────

    private fun createSilentNotification(): Notification {
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
        return NotificationCompat.Builder(this, SILENT_CHANNEL_ID)
            .setContentTitle("أذان")
            .setContentText("")
            .setSmallIcon(R.drawable.mosque_02)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_close_circle, "إيقاف", stopIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun createAzanNotification(prayer: Prayer.PrayerName): Notification {
        val fullScreenIntent = PendingIntent.getActivity(
            this, 0,
            AzanFullScreenActivity.newIntent(this, prayer.getArabicName()),
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
            .setSmallIcon(R.drawable.mosque_02)
            .setContentTitle("أذان ${prayer.getArabicName()}")
            .setContentText("حان وقت الصلاة")
            .setFullScreenIntent(fullScreenIntent, true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(R.drawable.ic_close_circle, "إيقاف", stopIntent)
            .build()
    }

    // ── Play ──────────────────────────────────────────────────────────────

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
                PrayerAlarmService.isPlaying = false
                if (DhikrService.isRunning) {
                    startService(Intent(this@PrayerAlarmService, DhikrService::class.java).apply {
                        action = DhikrService.ACTION_RESUME_FOR_AZAN
                    })
                }
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
