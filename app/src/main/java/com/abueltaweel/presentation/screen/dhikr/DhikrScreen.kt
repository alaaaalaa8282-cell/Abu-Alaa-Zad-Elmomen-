package com.abueltaweel.presentation.screen.dhikr

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abueltaweel.R
import org.koin.androidx.compose.koinViewModel

private val Gold  = Color(0xFFC9A84C)
private val Dark  = Color(0xFF0D1B2A)
private val Card  = Color(0xFF1B3A4B)

@Composable
fun DhikrScreen(viewModel: DhikrViewModel = koinViewModel()) {
    val state   by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Dark)
    ) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Card, Dark)))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painterResource(R.drawable.ic_azkar), null,
                    tint = Gold, modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text("أذكاري", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    "تُشغَّل الأذكار بالتسلسل ثم تعيد من الأول",
                    fontSize = 12.sp, color = Color(0xFFB0BEC5),
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // الترتيب الحالي للأذكار
            item {
                Text("ترتيب الأذكار", color = Gold, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    allDhikrs.forEachIndexed { index, dhikr ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Card)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(28.dp)
                                    .background(Gold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${index + 1}", color = Color.Black,
                                    fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(dhikr.textAr, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }

            // الفترة الزمنية
            item {
                Spacer(Modifier.height(4.dp))
                Text("الفترة بين كل ذكر", color = Gold, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(intervalOptions) { option ->
                        val selected = state.selectedInterval.minutes == option.minutes
                        val bg by animateColorAsState(
                            if (selected) Gold else Card, label = "interval"
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(bg)
                                .clickable(enabled = !state.isRunning) {
                                    viewModel.selectInterval(option)
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                option.label,
                                color = if (selected) Color.Black else Color.White,
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // سلايد الصوت
            item {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("مستوى الصوت", color = Gold, fontSize = 14.sp,
                        fontWeight = FontWeight.Bold)
                    Text("${(state.volume * 100).toInt()}%", color = Color.White,
                        fontSize = 13.sp)
                }
                Slider(
                    value = state.volume,
                    onValueChange = { viewModel.setVolume(it) },
                    enabled = !state.isRunning,
                    colors = SliderDefaults.colors(
                        thumbColor = Gold,
                        activeTrackColor = Gold,
                        inactiveTrackColor = Card
                    )
                )
            }

            // زرار التشغيل/الإيقاف
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (state.isRunning) viewModel.stop(context)
                        else viewModel.start(context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isRunning) Color(0xFFB00020) else Gold
                    ),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painterResource(
                            if (state.isRunning) R.drawable.ic_pause else R.drawable.ic_play
                        ),
                        null, tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (state.isRunning) "إيقاف الأذكار" else "ابدأ الأذكار",
                        color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                }

                if (state.isRunning) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "✓ الأذكار تعمل في الخلفية — كل ${state.selectedInterval.label}",
                        color = Color(0xFF4CAF50), fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
