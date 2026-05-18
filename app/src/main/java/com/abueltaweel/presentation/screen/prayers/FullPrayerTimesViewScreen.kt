package com.abueltaweel.presentation.screen.prayers

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.abueltaweel.R
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.screen.prayers.component.NextPrayerCard
import com.abueltaweel.presentation.screen.prayers.component.PrayerItem
import com.abueltaweel.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("BatteryLife")
@ExperimentalTime
@Composable
fun FullPrayerTimesViewScreen(
    navController: NavController,
    viewModel: FullPrayerTimesViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val countdownTime by viewModel.countdownTime.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            FullPrayerTimesEffect.RequestExactAlarm -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
            FullPrayerTimesEffect.RequestNotificationPermission -> {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            FullPrayerTimesEffect.RequestIgnoreBatteryOptimization -> {
                context.startActivity(
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                )
            }
            FullPrayerTimesEffect.RequestXiaomiAutoStart -> {
                openXiaomiAutoStart(context)
            }
            FullPrayerTimesEffect.NavigateBack -> {
                navController.popBackStack()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MosqueColors.DarkBg)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .border(3.dp, MosqueColors.Border, RoundedCornerShape(4.dp))
                .border(6.dp, MosqueColors.Gold.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MosqueColors.Creamy)
            ) {
                item { MosqueArchHeader() }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MosqueColors.Brown)
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = localizedString(R.string.prayer_times),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MosqueColors.Gold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                item { IslamicDivider() }

                item {
                    NextPrayerCard(
                        state = state,
                        countdownTime = countdownTime,
                    )
                }

                item { IslamicDivider() }

                items(state.prayers) {
                    PrayerItem(
                        prayerNameResource = it.name,
                        prayerTime = it.time.time,
                        isAm = it.time.isAm,
                        isNextPrayer = it.isUpComing,
                        isNotificationEnabled = it.isNotificationEnabled,
                        onNotificationClick = { prayerName, enabled ->
                            viewModel.onClickEnablePrayer(prayerName, enabled)
                        }
                    )
                }

                item { IslamicDivider() }
                item { BottomIslamicDecoration() }
            }

            MosqueColumns()
        }
    }
}

// ── قوس المحراب ───────────────────────────────────────────────────────────────
@Composable
fun MosqueArchHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)  // أكبر من قبل
            .background(MosqueColors.Brown)
    ) {
        // نقوش على الخلفية البنية
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBrownBackground(size)
        }

        // زخارف الكورنرات — أكبر
        CornerDecoration(modifier = Modifier.align(Alignment.TopStart))
        CornerDecoration(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .graphicsLayer { scaleX = -1f }
        )

        // القوس الكريمي
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawMosqueArch(size)
        }

        // الدعاء جوا القوس
        Box(
            modifier = Modifier
                .fillMaxWidth(0.60f)
                .align(Alignment.Center)
                .padding(top = 25.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "اللهم اغفر للمرحوم\nمحمد عبد العظيم طرفايه\nوارحمه وعافه واعف عنه\nواجعل قبره روضة من رياض الجنة\nواغفر له ذنوبه وزد حسناته",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC9A84C),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

// ── نقوش الخلفية البنية ───────────────────────────────────────────────────────
fun DrawScope.drawBrownBackground(size: Size) {
    val archWidth = size.width * 0.65f
    val archLeft  = (size.width - archWidth) / 2f

    // نقوش على الجانبين بس (خارج القوس)
    val dotStep = 14.dp.toPx()
    var dx = dotStep / 2
    while (dx < size.width) {
        // رسم النقاط على الجانبين فقط خارج منطقة القوس
        if (dx < archLeft - 5.dp.toPx() || dx > archLeft + archWidth + 5.dp.toPx()) {
            var dy = dotStep / 2
            while (dy < size.height) {
                drawCircle(
                    color = Color(0xFFC9A84C).copy(alpha = 0.18f),
                    radius = 2.dp.toPx(),
                    center = Offset(dx, dy)
                )
                dy += dotStep
            }
        }
        dx += dotStep
    }

    // خطوط أفقية زخرفية على الجانبين
    val lineCount = 5
    for (i in 0..lineCount) {
        val y = size.height * i / lineCount.toFloat()
        // يسار
        drawLine(
            color = Color(0xFFC9A84C).copy(alpha = 0.2f),
            start = Offset(0f, y),
            end = Offset(archLeft - 4.dp.toPx(), y),
            strokeWidth = 0.5.dp.toPx()
        )
        // يمين
        drawLine(
            color = Color(0xFFC9A84C).copy(alpha = 0.2f),
            start = Offset(archLeft + archWidth + 4.dp.toPx(), y),
            end = Offset(size.width, y),
            strokeWidth = 0.5.dp.toPx()
        )
    }
}

// ── رسم القوس ─────────────────────────────────────────────────────────────────
fun DrawScope.drawMosqueArch(size: Size) {
    val archWidth  = size.width * 0.65f
    val archLeft   = (size.width - archWidth) / 2f
    val archRadius = archWidth / 2f

    // الجزء المستطيل
    drawRect(
        color = Color(0xFFF5E6C0),
        topLeft = Offset(archLeft, size.height * 0.35f),
        size = Size(archWidth, size.height * 0.70f)
    )

    // القبة
    drawArc(
        color = Color(0xFFF5E6C0),
        startAngle = 180f, sweepAngle = 180f, useCenter = true,
        topLeft = Offset(archLeft, size.height * 0.35f - archRadius),
        size = Size(archWidth, archWidth)
    )

    // حد ذهبي على القبة
    drawArc(
        color = Color(0xFFC9A84C),
        startAngle = 180f, sweepAngle = 180f, useCenter = false,
        topLeft = Offset(archLeft, size.height * 0.35f - archRadius),
        size = Size(archWidth, archWidth),
        style = Stroke(width = 3.dp.toPx())
    )

    // حد ذهبي ثاني داخلي
    drawArc(
        color = Color(0xFFC9A84C).copy(alpha = 0.4f),
        startAngle = 180f, sweepAngle = 180f, useCenter = false,
        topLeft = Offset(archLeft + 6.dp.toPx(), size.height * 0.35f - archRadius + 6.dp.toPx()),
        size = Size(archWidth - 12.dp.toPx(), archWidth - 12.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )

    // خطوط جانبية القوس
    drawLine(color = Color(0xFFC9A84C), start = Offset(archLeft, size.height * 0.35f), end = Offset(archLeft, size.height), strokeWidth = 3.dp.toPx())
    drawLine(color = Color(0xFFC9A84C), start = Offset(archLeft + archWidth, size.height * 0.35f), end = Offset(archLeft + archWidth, size.height), strokeWidth = 3.dp.toPx())

    // خطوط جانبية داخلية
    drawLine(color = Color(0xFFC9A84C).copy(alpha = 0.4f), start = Offset(archLeft + 6.dp.toPx(), size.height * 0.35f), end = Offset(archLeft + 6.dp.toPx(), size.height), strokeWidth = 1.dp.toPx())
    drawLine(color = Color(0xFFC9A84C).copy(alpha = 0.4f), start = Offset(archLeft + archWidth - 6.dp.toPx(), size.height * 0.35f), end = Offset(archLeft + archWidth - 6.dp.toPx(), size.height), strokeWidth = 1.dp.toPx())

    // نقاط ذهبية على حافة القبة
    val centerX = size.width / 2f
    val centerY = size.height * 0.35f
    for (i in 0..10) {
        val angle = Math.PI * i / 10.0
        val x = (centerX - archRadius * Math.cos(angle)).toFloat()
        val y = (centerY - archRadius * Math.sin(angle)).toFloat()
        drawCircle(color = Color(0xFFC9A84C), radius = 4.dp.toPx(), center = Offset(x, y))
    }
}

// ── زخرفة الكورنر — أكبر وأجمل ───────────────────────────────────────────────
@Composable
fun CornerDecoration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(90.dp)) {
        val s = size.minDimension

        // مربعات متداخلة
        for (i in 0..3) {
            val inset = i * 7.dp.toPx()
            drawRect(
                color = Color(0xFFC9A84C).copy(alpha = 1f - i * 0.2f),
                topLeft = Offset(inset, inset),
                size = Size(s - inset * 2, s - inset * 2),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // خطوط الحافة
        drawLine(color = Color(0xFFC9A84C), start = Offset(0f, 0f), end = Offset(s * 0.6f, 0f), strokeWidth = 2.5.dp.toPx())
        drawLine(color = Color(0xFFC9A84C), start = Offset(0f, 0f), end = Offset(0f, s * 0.6f), strokeWidth = 2.5.dp.toPx())

        // دائرة زخرفية
        drawCircle(color = Color(0xFFC9A84C), radius = 8.dp.toPx(), center = Offset(s * 0.28f, s * 0.28f), style = Stroke(width = 1.5.dp.toPx()))
        drawCircle(color = Color(0xFFC9A84C), radius = 3.dp.toPx(), center = Offset(s * 0.28f, s * 0.28f))

        // نجمة صغيرة جوا الدائرة
        val cx = s * 0.28f
        val cy = s * 0.28f
        val r  = 5.dp.toPx()
        for (i in 0..3) {
            val angle = Math.PI * i / 4.0
            drawLine(
                color = Color(0xFFC9A84C).copy(alpha = 0.7f),
                start = Offset((cx - r * Math.cos(angle)).toFloat(), (cy - r * Math.sin(angle)).toFloat()),
                end   = Offset((cx + r * Math.cos(angle)).toFloat(), (cy + r * Math.sin(angle)).toFloat()),
                strokeWidth = 1.dp.toPx()
            )
        }

        // زخرفة نباتية بسيطة — منحنيات
        val path = Path().apply {
            moveTo(s * 0.5f, 0f)
            quadraticBezierTo(s * 0.65f, s * 0.2f, s * 0.5f, s * 0.35f)
            moveTo(0f, s * 0.5f)
            quadraticBezierTo(s * 0.2f, s * 0.65f, s * 0.35f, s * 0.5f)
        }
        drawPath(path, color = Color(0xFFC9A84C).copy(alpha = 0.5f), style = Stroke(width = 1.dp.toPx()))
    }
}

// ── فاصل إسلامي ───────────────────────────────────────────────────────────────
@Composable
fun IslamicDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            val midY = size.height / 2f
            val midX = size.width / 2f
            drawLine(color = Color(0xFFC9A84C), start = Offset(0f, midY), end = Offset(size.width, midY), strokeWidth = 1.dp.toPx())
            val starR = 5.dp.toPx()
            for (i in 0..3) {
                val angle = Math.PI * i / 4.0
                drawLine(
                    color = Color(0xFFC9A84C),
                    start = Offset((midX - starR * Math.cos(angle)).toFloat(), (midY - starR * Math.sin(angle)).toFloat()),
                    end = Offset((midX + starR * Math.cos(angle)).toFloat(), (midY + starR * Math.sin(angle)).toFloat()),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
            listOf(0.15f, 0.25f, 0.35f, 0.65f, 0.75f, 0.85f).forEach { pos ->
                drawCircle(color = Color(0xFFC9A84C), radius = 2.dp.toPx(), center = Offset(size.width * pos, midY))
            }
        }
    }
}

// ── أعمدة — أعرض ──────────────────────────────────────────────────────────────
@Composable
fun MosqueColumns() {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(2) {
            Canvas(modifier = Modifier.width(14.dp).fillMaxHeight()) {
                // خلفية العمود
                drawRect(color = Color(0xFF5A1A00).copy(alpha = 0.8f))
                // خطوط أفقية
                for (i in 0..30) {
                    val y = size.height * i / 30f
                    drawLine(
                        color = Color(0xFFC9A84C).copy(alpha = 0.3f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }
                // حد ذهبي
                drawRect(color = Color(0xFFC9A84C), style = Stroke(width = 1.dp.toPx()))
                // خط ذهبي وسط العمود
                drawLine(
                    color = Color(0xFFC9A84C).copy(alpha = 0.5f),
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, size.height),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
        }
    }
}

// ── زخرفة سفلية ───────────────────────────────────────────────────────────────
@Composable
fun BottomIslamicDecoration() {
    Box(
        modifier = Modifier.fillMaxWidth().height(40.dp).background(MosqueColors.Brown),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val midY = size.height / 2f
            val step = 18.dp.toPx()
            var x = step / 2
            while (x < size.width) {
                drawCircle(color = Color(0xFFC9A84C), radius = 3.dp.toPx(), center = Offset(x, midY))
                drawLine(color = Color(0xFFC9A84C).copy(alpha = 0.5f), start = Offset(x - step / 2, midY), end = Offset(x + step / 2, midY), strokeWidth = 1.dp.toPx())
                // نجمة صغيرة كل 3 نقاط
                if ((x / step).toInt() % 3 == 1) {
                    for (i in 0..3) {
                        val angle = Math.PI * i / 4.0
                        val r = 4.dp.toPx()
                        drawLine(
                            color = Color(0xFFC9A84C).copy(alpha = 0.6f),
                            start = Offset((x - r * Math.cos(angle)).toFloat(), (midY - r * Math.sin(angle)).toFloat()),
                            end = Offset((x + r * Math.cos(angle)).toFloat(), (midY + r * Math.sin(angle)).toFloat()),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
                x += step
            }
            // خطان أفقيان
            drawLine(color = Color(0xFFC9A84C).copy(alpha = 0.4f), start = Offset(0f, midY - 8.dp.toPx()), end = Offset(size.width, midY - 8.dp.toPx()), strokeWidth = 0.5.dp.toPx())
            drawLine(color = Color(0xFFC9A84C).copy(alpha = 0.4f), start = Offset(0f, midY + 8.dp.toPx()), end = Offset(size.width, midY + 8.dp.toPx()), strokeWidth = 0.5.dp.toPx())
        }
    }
}

fun openXiaomiAutoStart(context: Context) {
    try {
        val intent = Intent().apply {
            component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
        }
        context.startActivity(intent)
    } catch (e: Exception) {}
}

@SuppressLint("BatteryLife")
suspend fun checkAndRequestPermissions(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            return
        }
    }
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
        context.startActivity(
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = "package:${context.packageName}".toUri()
            }
        )
        return
    }
    if (Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)) {
        try {
            val intent = Intent().apply {
                component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
