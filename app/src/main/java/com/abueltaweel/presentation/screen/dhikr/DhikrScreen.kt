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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.abueltaweel.R
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrScreen(viewModel: DhikrViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var showStartPicker by remember { mutableStateOf(false) }
    var showStopPicker  by remember { mutableStateOf(false) }

    val startTimePickerState = rememberTimePickerState(
        initialHour = state.startHour, initialMinute = state.startMinute, is24Hour = false 
    )
    val stopTimePickerState = rememberTimePickerState(
        initialHour = state.stopHour, initialMinute = state.stopMinute, is24Hour = false 
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.syncServiceState(context)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    // ─── Start Time Dialog ───
    if (showStartPicker) {
        AlertDialog(
            onDismissRequest = { showStartPicker = false },
            containerColor = Color(0xFF1B3A4B),
            title = { Text("وقت بدء الأذكار", color = Color(0xFFC9A84C), fontWeight = FontWeight.Bold) },
            text = {
                TimePicker(
                    state = startTimePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFF0D1B2A),
                        clockDialSelectedContentColor = Color.Black,
                        clockDialUnselectedContentColor = Color.White,
                        selectorColor = Color(0xFFC9A84C),
                        periodSelectorSelectedContainerColor = Color(0xFFC9A84C),
                        timeSelectorSelectedContainerColor = Color(0xFFC9A84C),
                        timeSelectorUnselectedContainerColor = Color(0xFF0D1B2A),
                        timeSelectorSelectedContentColor = Color.Black,
                        timeSelectorUnselectedContentColor = Color.White
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setStartTime(startTimePickerState.hour, startTimePickerState.minute, context)
                    showStartPicker = false
                }) { Text("حفظ", color = Color(0xFFC9A84C)) }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            }
        )
    }

    // ─── Stop Time Dialog ───
    if (showStopPicker) {
        AlertDialog(
            onDismissRequest = { showStopPicker = false },
            containerColor = Color(0xFF1B3A4B),
            title = { Text("وقت إيقاف الأذكار", color = Color(0xFFC9A84C), fontWeight = FontWeight.Bold) },
            text = {
                TimePicker(
                    state = stopTimePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFF0D1B2A),
                        clockDialSelectedContentColor = Color.Black,
                        clockDialUnselectedContentColor = Color.White,
                        selectorColor = Color(0xFFC9A84C),
                        timeSelectorSelectedContainerColor = Color(0xFFC9A84C),
                        timeSelectorUnselectedContainerColor = Color(0xFF0D1B2A),
                        timeSelectorSelectedContentColor = Color.Black,
                        timeSelectorUnselectedContentColor = Color.White
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setStopTime(stopTimePickerState.hour, stopTimePickerState.minute, context)
                    showStopPicker = false
                }) { Text("حفظ", color = Color(0xFFC9A84C)) }
            },
            dismissButton = {
                TextButton(onClick = { showStopPicker = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0D1B2A))
    ) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF1B3A4B), Color(0xFF0D1B2A))))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(painterResource(R.drawable.ic_tasbih), null, tint = Color(0xFFC9A84C), modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text("أذكاري", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("تُشغِّل الأذكار بالتسلسل ثم تعيد من الأول", fontSize = 13.sp, color = Color(0xFFB0BEC5))
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ─── قائمة الأذكار ───
            item {
                Text("اختر الذكر", color = Color(0xFFC9A84C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            items(state.dhikrList, key = { it.id }) { item ->
                val selected = state.selectedDhikr.id == item.id
                val bg by animateColorAsState(
                    if (selected) Color(0x44C9A84C) else Color(0xFF1B3A4B), label = "bg"
                )
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .clickable(enabled = !state.isRunning) { viewModel.selectDhikr(item) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selected) {
                        Box(Modifier.size(22.dp).background(Color(0xFFC9A84C), CircleShape), Alignment.Center) {
                            Icon(painterResource(R.drawable.check), null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                    }
                    Text(item.textAr, color = if (selected) Color(0xFFC9A84C) else Color.White, fontSize = 16.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                }
            }

            // ─── الفترة بين كل ذكر ───
            item {
                Spacer(Modifier.height(4.dp))
                val mins = state.intervalSec / 60
                val secs = state.intervalSec % 60
                val lbl = when { mins > 0 && secs > 0 -> "$mins د $secs ث"; mins > 0 -> "$mins دقيقة"; else -> "$secs ثانية" }
                Text("الفترة بين كل ذكر: $lbl", color = Color(0xFFC9A84C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("1 د" to 60, "5 د" to 300, "10 د" to 600, "30 د" to 1800).forEach { (l, sec) ->
                        val isSel = state.intervalSec == sec
                        Button(
                            onClick = { if (!state.isRunning) viewModel.setInterval(sec) },
                            enabled = !state.isRunning,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) Color(0xFFC9A84C) else Color(0xFF1B3A4B),
                                contentColor = if (isSel) Color.Black else Color(0xFFC9A84C),
                                disabledContainerColor = if (isSel) Color(0x99C9A84C) else Color(0xFF1B3A4B),
                                disabledContentColor = if (isSel) Color.Black else Color(0x66C9A84C)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text(l, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }

            // ─── مستوى الصوت ───
            item {
                Text("مستوى الصوت: ${(state.volume * 100).roundToInt()}%",
                    color = Color(0xFFC9A84C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = state.volume, onValueChange = { viewModel.setVolume(it) },
                    valueRange = 0f..1f, modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFC9A84C), activeTrackColor = Color(0xFFC9A84C), inactiveTrackColor = Color(0xFF1B3A4B))
                )
            }

            // ─── التشغيل التلقائي ───
            item {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B3A4B)).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("تشغيل تلقائي يومي", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = state.autoEnabled,
                        onCheckedChange = { viewModel.setAutoEnabled(it, context) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color(0xFFC9A84C))
                    )
                }

                if (state.autoEnabled) {
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        // وقت البداية
                        Button(
                            onClick = { showStartPicker = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3A4B)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("بداية", color = Color(0xFFC9A84C), fontSize = 12.sp)
                                Text(
                                    "%d:%02d %s".format(if (state.startHour % 12 == 0) 12 else state.startHour % 12, state.startMinute, if (state.startHour < 12) "ص" else "م")
                                    color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        // وقت النهاية
                        Button(
                            onClick = { showStopPicker = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3A4B)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("نهاية", color = Color(0xFFC9A84C), fontSize = 12.sp)
                                Text(
                                    "%d:%02d %s".format(if (state.stopHour % 12 == 0) 12 else state.stopHour % 12, state.stopMinute, if (state.stopHour < 12) "ص" else "م") 
                                    color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // ─── زرار التشغيل/الإيقاف ───
            item {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { if (state.isRunning) viewModel.stop(context) else viewModel.start(context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isRunning) Color(0xFFB00020) else Color(0xFFC9A84C)
                    ),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(painterResource(if (state.isRunning) R.drawable.ic_stop else R.drawable.ic_play),
                        null, tint = Color.Black, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (state.isRunning) "إيقاف الأذكار" else "ابدأ الأذكار",
                        color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                if (state.isRunning) {
                    Spacer(Modifier.height(10.dp))
                    Text("الأذكار تعمل في الخلفية", color = Color(0xFF4CAF50),
                        fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
