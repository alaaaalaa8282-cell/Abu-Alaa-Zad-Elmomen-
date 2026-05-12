package com.abueltaweel.presentation.screen.azan

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abueltaweel.R
import com.abueltaweel.presentation.service.PrayerAlarmService
import com.abueltaweel.presentation.utils.Constants
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale

class AzanFullScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager)
                .requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val prayerName = intent.getStringExtra(Constants.PRAYER_NAME_KEY) ?: "الصلاة"
        setContent {
            AzanFullScreenContent(prayerName = prayerName, onStop = {
                startService(Intent(this, PrayerAlarmService::class.java).apply {
                    action = Constants.ACTION_STOP_AZAN
                })
                finish()
            })
        }
    }

    companion object {
        fun newIntent(context: Context, prayerName: String) =
            Intent(context, AzanFullScreenActivity::class.java).apply {
                putExtra(Constants.PRAYER_NAME_KEY, prayerName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            }
    }
}

@Composable
fun AzanFullScreenContent(prayerName: String, onStop: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "scale"
    )
    Box(
        modifier = Modifier.fillMaxSize(),
            
        contentAlignment = Alignment.Center
    ) {
      Image(painter = painterResource(R.drawable.father_photo), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
     Box(modifier = Modifier.fillMaxSize().background(Color(0xCC000000)))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(32.dp)
        ) {
            Box(
                modifier = Modifier.scale(scale).size(130.dp).background(Color(0x33C9A84C), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.mosque_02), null, tint = Color(0xFFC9A84C), modifier = Modifier.size(75.dp))
            }
            Spacer(Modifier.height(36.dp))
            Text("حَيَّ عَلَى الصَّلَاةِ", fontSize = 30.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFFC9A84C), textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Text("أذان $prayerName", fontSize = 24.sp, fontWeight = FontWeight.Medium,
                color = Color.White, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ", fontSize = 16.sp,
                color = Color(0xFFB0BEC5), textAlign = TextAlign.Center)
            Spacer(Modifier.height(60.dp))
            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC9A84C)),
                modifier = Modifier.fillMaxWidth(0.6f).height(52.dp),
                shape = CircleShape
            ) {
                Text("إيقاف الأذان", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
