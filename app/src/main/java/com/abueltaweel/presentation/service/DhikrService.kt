package com.abueltaweel.presentation.service

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
import com.abueltaweel.presentation.base.MainActivity
import kotlinx.coroutines.*

class DhikrService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var dhikrResIds  = intArrayOf()
    private var dhikrTexts   = arrayOf<String>()
    private var intervalMs   = 5 * 60 * 1000L
    private var volume       = 1f
    private var currentIndex = 0
    private var running      = false

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopDhikr()
            return START_NOT_STICKY
        }

        dhikrResIds  = intent?.getIntArrayExtra(EXTRA_RES_IDS) ?: return START_NOT_STICKY
        dhikrTexts   = intent.getStringArrayExtra(EXTRA_TEXTS) ?: arrayOf()
        intervalMs   = intent.getLongExtra(EXTRA_INTERVAL_MS, 5 * 60 * 1000L)
        volume       = intent.getFloatExtra(EXTRA_VOLUME, 1f)
        currentIndex = 0
        running      = true
        isRunning    = true   // ← static flag

        acquireWakeLock()
        createChannel()
        startForeground(NOTIF_ID, buildNotification(currentIndex))
        playCurrentDhikr()

        return START_STICKY
    }

    private fun playCurrentDhikr() {
        if (!running || dhikrResIds.isEmpty()) return

        val resId = dhikrResIds[currentIndex]
        val logVol = if (volume <= 0f) 0f
        else (1 - (Math.log((1 + (1 - volume) * 99).toDouble()) / Math.log(100.0))).toFloat()

        updateNotification(currentIndex)

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, resId)?.apply {
                setVolume(logVol, logVol)
                setWakeMode(this@DhikrService, PowerManager.PARTIAL_WAKE_LOCK)
                setOnCompletionListener {
                    scope.launch {
                        delay(intervalMs)
                        if (running) {
                            currentIndex = (currentIndex + 1) % dhikrResIds.size
                            playCurrentDhikr()
                        }
                    }
                }
                setOnErrorListener { _, _, _ ->
                    scope.launch {
                        delay(intervalMs)
                        if (running) {
                            currentIndex = (currentIndex + 1) % dhikrResIds.size
                            playCurrentDhikr()
                        }
                    }
                    true
                }
                start()
            }
        } catch (e: Exception) {
            scope.launch {
                delay(intervalMs)
                if (running) {
                    currentIndex = (currentIndex + 1) % dhikrResIds.size
                    playCurrentDhikr()
                }
            }
        }
    }

    private fun stopDhikr() {
        running   = false
        isRunning = false   // ← static flag
        scope.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        releaseWakeLock()
        try { stopForeground(true) } catch (_: Exception) {}
        stopSelf()
    }

    private fun acquireWakeLock() {
        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "AbuEltaweel:DhikrWakeLock"
            ).also { it.acquire(6 * 60 * 60 * 1000L) }
        } catch (_: Exception) {}
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) wakeLock?.release()
        } catch (_: Exception) {}
        wakeLock = null
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID, "الأذكار الصوتية",
                        NotificationManager.IMPORTANCE_LOW
                    ).apply { setSound(null, null); enableVibration(false) }
                )
            }
        }
    }

    private fun buildNotification(index: Int): Notification {
        val text = if (dhikrTexts.isNotEmpty() && index < dhikrTexts.size)
            dhikrTexts[index] else "جارٍ التشغيل..."

        val stopPi = PendingIntent.getService(
            this, 0,
            Intent(this, DhikrService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val openPi = PendingIntent.getActivity(
    this, 1,
    Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    },
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_azkar)
            .setContentTitle("أذكاري — ${index + 1}/${dhikrResIds.size}")
            .setContentText(text)
            .setContentIntent(openPi)
            .setOngoing(true)
            .addAction(R.drawable.ic_close_circle, "إيقاف", stopPi)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(index: Int) {
        try {
            val nm = getSystemService(NotificationManager::class.java)
            nm.notify(NOTIF_ID, buildNotification(index))
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        running   = false
        isRunning = false   // ← static flag
        scope.cancel()
        try { mediaPlayer?.release() } catch (_: Exception) {}
        mediaPlayer = null
        releaseWakeLock()
        super.onDestroy()
    }
  override fun onTaskRemoved(rootIntent: Intent?) {
    val restart = Intent(applicationContext, DhikrService::class.java).apply {
        putExtra(EXTRA_RES_IDS, dhikrResIds)
        putExtra(EXTRA_TEXTS, dhikrTexts)
        putExtra(EXTRA_INTERVAL_MS, intervalMs)
        putExtra(EXTRA_VOLUME, volume)
    }
    startService(restart)
    super.onTaskRemoved(rootIntent)
  }
    
    companion object {
        const val CHANNEL_ID        = "dhikr_channel"
        const val NOTIF_ID          = 42
        const val ACTION_STOP       = "com.abueltaweel.STOP_DHIKR"
        const val EXTRA_RES_IDS     = "dhikr_res_ids"
        const val EXTRA_TEXTS       = "dhikr_texts"
        const val EXTRA_INTERVAL_MS = "dhikr_interval_ms"
        const val EXTRA_VOLUME      = "dhikr_volume"

        // ← static flag يقدر الـ ViewModel يقرأه
        @Volatile var isRunning: Boolean = false
    }
}
