package com.abueltaweel.presentation.screen.azan

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abueltaweel.R
import com.abueltaweel.presentation.service.PrayerAlarmService
import com.abueltaweel.presentation.utils.Constants
import kotlinx.coroutines.delay

class AzanFullScreenActivity : ComponentActivity() {

    private val azanDoneReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

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

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        registerReceiver(azanDoneReceiver, IntentFilter(Constants.ACTION_STOP_AZAN))
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        unregisterReceiver(azanDoneReceiver)
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

    val images = listOf(
        R.drawable.mosque_bg,
        R.drawable.mosque_bg1,
        R.drawable.mosque_bg2,
        R.drawable.mosque_bg3,
        R.drawable.mosque_bg4,
        R.drawable.mosque_bg5,
        R.drawable.father_bg,
        R.drawable.father_bg1,
        R.drawable.father_bg2,
        R.drawable.father_bg3,
        R.drawable.father_bg4,
        R.drawable.father_bg5,
        R.drawable.father_bg6,
        R.drawable.father_bg7,
    )

    var currentIndex by remember { mutableStateOf(0) }
    var nextIndex by remember { mutableStateOf(1) }
    var crossfadeAlpha by remember { mutableStateOf(0f) }

    val animCrossfade by animateFloatAsState(
        targetValue = crossfadeAlpha,
        animationSpec = tween(2000),
        label = "crossfade"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            crossfadeAlpha = 1f
            delay(2000)
            currentIndex = nextIndex
            nextIndex = (nextIndex + 1) % images.size
            crossfadeAlpha = 0f
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow"
    )

    val azanLines = listOf(
        "اللهُ أَكْبَر، اللهُ أَكْبَر",
        "أَشْهَدُ أَن لَّا إِلَٰهَ إِلَّا اللَّه",
        "أَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ اللَّه",
        "حَيَّ عَلَى الصَّلَاة",
        "حَيَّ عَلَى الْفَلَاح",
        "اللهُ أَكْبَر",
        "لَا إِلَٰهَ إِلَّا اللَّه"
    )

    var currentLineIndex by remember { mutableStateOf(0) }
    var lineVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            lineVisible = false
            delay(600)
            currentLineIndex = (currentLineIndex + 1) % azanLines.size
            lineVisible = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // الصورة الحالية
        Image(
            painter = painterResource(images[currentIndex]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // الصورة الجديدة بتظهر فوقها بـ crossfade
        Image(
            painter = painterResource(images[nextIndex]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(animCrossfade)
        )

        // gradient داكن
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color(0x66000000), Color(0xBB000000), Color(0xEE000000))
                )
            )
        )

        // دايرة توهج ذهبية
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(280.dp)
                .background(Color(0xFFC9A84C).copy(alpha = glowAlpha * 0.12f), CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(32.dp)
        ) {

            // أيقونة المسجد النابضة
            Box(
                modifier = Modifier.scale(scale).size(130.dp)
                    .background(Color(0x33C9A84C), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.mosque_02), null,
                    tint = Color(0xFFC9A84C),
                    modifier = Modifier.size(75.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            // اسم الصلاة
            Text(
                "أذان $prayerName",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC9A84C),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            // نص الأذان بيتغير
            AnimatedVisibility(
                visible = lineVisible,
                enter = fadeIn(tween(600)) + slideInVertically { it / 3 },
                exit = fadeOut(tween(600)) + slideOutVertically { -it / 3 }
            ) {
                Text(
                    azanLines[currentLineIndex],
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا",
                fontSize = 13.sp,
                color = Color(0xFFB0BEC5),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(56.dp))

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
