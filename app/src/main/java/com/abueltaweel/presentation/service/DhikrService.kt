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

    private var dhikrText    = ""
    private var rawResId     = 0
    private var totalCount   = 33
    private var intervalMs   = 3000L
    private var currentCount = 0
    private var isRunning    = false

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) { stopDhikr(); return START_NOT_STICKY }

        dhikrText  = intent?.getStringExtra(EXTRA_TEXT)     ?: return START_NOT_STICKY
        rawResId   = intent.getIntExtra(EXTRA_RES_ID, 0).also { if (it == 0) return START_NOT_STICKY }
        totalCount = intent.getIntExtra(EXTRA_COUNT, 33)
        intervalMs = intent.getLongExtra(EXTRA_INTERVAL_MS, 3000L)
        currentCount = 0
        isRunning    = true

        acquireWakeLock()
        createChannel()
        startForeground(NOTIF_ID, buildNotification())
        playNext()

        return START_NOT_STICKY
    }

    private fun playNext() {
        if (!isRunning || currentCount >= totalCount) { stopDhikr(); return }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, rawResId)?.apply {
            setWakeMode(this@DhikrService, PowerManager.PARTIAL_WAKE_LOCK)
            setOnCompletionListener {
                currentCount++
                updateNotification()
                if (currentCount >= totalCount) {
                    stopDhikr()
                } else {
                    scope.launch {
                        delay(intervalMs)
                        if (isRunning) playNext()
                    }
                }
            }
            start()
        }
    }

    private fun stopDhikr() {
        isRunning = false
        scope.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        wakeLock?.release()
        stopForeground(true)
        stopSelf()
        sendBroadcast(Intent(ACTION_DONE).setPackage(packageName))
    }

    private fun acquireWakeLock() {
        wakeLock = (getSystemService(POWER_SERVICE) as PowerManager)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AbuEltaweel:DhikrLock")
            .also { it.acquire(2 * 60 * 60 * 1000L) }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "الأذكار الصوتية", NotificationManager.IMPORTANCE_LOW)
                    .apply { setSound(null, null); enableVibration(false) }
            )
        }
    }

    private fun buildNotification(): Notification {
        val stopPi = PendingIntent.getService(this, 0,
            Intent(this, DhikrService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val openPi = PendingIntent.getActivity(this, 1,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_azkar)
            .setContentTitle(dhikrText)
            .setContentText("${currentCount} / ${totalCount}")
            .setProgress(totalCount, currentCount, false)
            .setContentIntent(openPi)
            .setOngoing(true)
            .addAction(R.drawable.ic_close_circle, "إيقاف", stopPi)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification() {
        getSystemService(NotificationManager::class.java).notify(NOTIF_ID, buildNotification())
    }

    override fun onDestroy() { stopDhikr(); super.onDestroy() }

    companion object {
        const val CHANNEL_ID       = "dhikr_channel"
        const val NOTIF_ID         = 42
        const val ACTION_STOP      = "com.abueltaweel.STOP_DHIKR"
        const val ACTION_DONE      = "com.abueltaweel.DHIKR_DONE"
        const val EXTRA_TEXT       = "dhikr_text"
        const val EXTRA_RES_ID     = "dhikr_res_id"
        const val EXTRA_COUNT      = "dhikr_count"
        const val EXTRA_INTERVAL_MS= "dhikr_interval_ms"
    }
}
