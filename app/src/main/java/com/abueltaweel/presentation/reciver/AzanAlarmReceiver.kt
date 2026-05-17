package com.abueltaweel.presentation.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abueltaweel.presentation.screen.azan.AzanFullScreenActivity
import com.abueltaweel.presentation.utils.Constants.PRAYER_NAME_KEY

class AzanAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName = when (intent?.getStringExtra(PRAYER_NAME_KEY)) {
            "FAJR"    -> "الفجر"
            "ZUHR"    -> "الظهر"
            "ASR"     -> "العصر"
            "MAGHRIB" -> "المغرب"
            "ISHA"    -> "العشاء"
            else      -> "الصلاة"
        }

        // FIX: شيلنا حساب isCallInProgress من هنا
        // الـ AzanFullScreenActivity بتحسبه بنفسها في onCreate
        val activityIntent = AzanFullScreenActivity.newIntent(context, prayerName).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }

        context.startActivity(activityIntent)
    }
}
