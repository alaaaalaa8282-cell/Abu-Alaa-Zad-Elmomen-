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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.abueltaweel.R
import com.abueltaweel.design_system.component.AppBar
import com.abueltaweel.design_system.theme.Theme
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.screen.prayers.component.NextPrayerCard
import com.abueltaweel.presentation.screen.prayers.component.PrayerItem
import com.abueltaweel.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime
import androidx.compose.ui.graphics.Color

// ── ألوان المؤذن الإلكتروني ──────────────────────────────────────────────────
val MosqueCreamy   = Color(0xFFF5E6C0)
val MosqueBrown    = Color(0xFF6B2A0A)
val MosqueGold     = Color(0xFFC9A84C)
val MosqueDarkBg   = Color(0xFF3D1A00)
val MosqueBorder   = Color(0xFF8B4513)

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

    // ── الإطار الخارجي الداكن ──────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MosqueDarkBg)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // ── الإطار الداخلي مع الزخارف ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .border(3.dp, MosqueBorder, RoundedCornerShape(4.dp))
                .border(6.dp, MosqueGold.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MosqueCreamy)
            ) {
                // ── القوس في الأعلى ──────────────────────────────────────────
                item {
                    MosqueArchHeader()
                }

                // ── عنوان الصفحة ──────────────────────────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MosqueBrown)
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = localizedString(R.string.prayer_times),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MosqueGold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // ── فاصل ذهبي ────────────────────────────────────────────────
                item { IslamicDivider() }

                // ── بطاقة الصلاة القادمة ──────────────────────────────────────
                item {
                    NextPrayerCard(
                        state = state,
                        countdownTime = countdownTime,
                    )
                }

                // ── فاصل ذهبي ────────────────────────────────────────────────
                item { IslamicDivider() }

                // ── الصلوات ───────────────────────────────────────────────────
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

                // ── فاصل ذهبي ────────────────────────────────────────────────
                item { IslamicDivider() }

                // ── زخرفة سفلية ───────────────────────────────────────────────
                item { BottomIslamicDecoration() }
            }

            // ── أعمدة على الجانبين ────────────────────────────────────────────
            MosqueColumns()
        }
    }
}

// ── قوس المحراب في الأعلى ────────────────────────────────────────────────────
@Composable
fun MosqueArchHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MosqueBrown)
    ) {
        // زخارف الكورنرات
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
                .fillMaxWidth(0.62f)
                .align(Alignment.Center)
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "اللهم اغفر للمرحوم\nمحمد عبد العظيم طرفايه\nوارحمه وعافه واعف عنه\nواجعل قبره روضة من رياض الجنة\nواغفر له ذنوبه وزد حسناته",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D1A00),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

fun DrawScope.drawMosqueArch(size: Size) {
    val archWidth  = size.width * 0.65f
    val archLeft   = (size.width - archWidth) / 2f
    val archBottom = size.height + 20f
    val archRadius = archWidth / 2f

    // الجزء المستطيل من الأسفل
    drawRect(
        color = Color(0xFFF5E6C0),
        topLeft = Offset(archLeft, size.height * 0.45f),
        size = Size(archWidth, size.height * 0.6f)
    )

    // القبة نصف الدائرة
    drawArc(
        color = Color(0xFFF5E6C0),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(archLeft, size.height * 0.45f - archRadius),
        size = Size(archWidth, archWidth)
    )

    // حدود القوس الذهبية
    drawArc(
        color = Color(0xFFC9A84C),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(archLeft, size.height * 0.45f - archRadius),
        size = Size(archWidth, archWidth),
        style = Stroke(width = 3.dp.toPx())
    )

    // خطوط جانبية للقوس
    drawLine(
        color = Color(0xFFC9A84C),
        start = Offset(archLeft, size.height * 0.45f),
        end = Offset(archLeft, size.height),
        strokeWidth = 3.dp.toPx()
    )
    drawLine(
        color = Color(0xFFC9A84C),
        start = Offset(archLeft + archWidth, size.height * 0.45f),
        end = Offset(archLeft + archWidth, size.height),
        strokeWidth = 3.dp.toPx()
    )

    // نقاط زخرفية على القوس
    val centerX = size.width / 2f
    val centerY = size.height * 0.45f
    for (i in 0..8) {
        val angle = Math.PI * i / 8.0
        val x = (centerX - archRadius * Math.cos(angle)).toFloat()
        val y = (centerY - archRadius * Math.sin(angle)).toFloat()
        drawCircle(
            color = Color(0xFFC9A84C),
            radius = 4.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

// ── زخرفة الكورنر ────────────────────────────────────────────────────────────
@Composable
fun CornerDecoration(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(70.dp)
    ) {
        val s = size.minDimension

        // مربعات متداخلة
        for (i in 0..2) {
            val inset = i * 6.dp.toPx()
            drawRect(
                color = Color(0xFFC9A84C).copy(alpha = 1f - i * 0.25f),
                topLeft = Offset(inset, inset),
                size = Size(s - inset * 2, s - inset * 2),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // خطوط قطرية
        drawLine(
            color = Color(0xFFC9A84C),
            start = Offset(0f, 0f),
            end = Offset(s * 0.5f, 0f),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color(0xFFC9A84C),
            start = Offset(0f, 0f),
            end = Offset(0f, s * 0.5f),
            strokeWidth = 2.dp.toPx()
        )

        // دائرة زخرفية
        drawCircle(
            color = Color(0xFFC9A84C),
            radius = 6.dp.toPx(),
            center = Offset(s * 0.25f, s * 0.25f),
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFC9A84C),
            radius = 2.dp.toPx(),
            center = Offset(s * 0.25f, s * 0.25f)
        )
    }
}

// ── فاصل إسلامي ذهبي ─────────────────────────────────────────────────────────
@Composable
fun IslamicDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        ) {
            val midY = size.height / 2f
            val midX = size.width / 2f

            // خط رئيسي
            drawLine(
                color = Color(0xFFC9A84C),
                start = Offset(0f, midY),
                end = Offset(size.width, midY),
                strokeWidth = 1.dp.toPx()
            )

            // نجمة وسط
            val starR = 5.dp.toPx()
            for (i in 0..3) {
                val angle = Math.PI * i / 4.0
                drawLine(
                    color = Color(0xFFC9A84C),
                    start = Offset(
                        (midX - starR * Math.cos(angle)).toFloat(),
                        (midY - starR * Math.sin(angle)).toFloat()
                    ),
                    end = Offset(
                        (midX + starR * Math.cos(angle)).toFloat(),
                        (midY + starR * Math.sin(angle)).toFloat()
                    ),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // نقاط جانبية
            val dots = listOf(0.2f, 0.3f, 0.7f, 0.8f)
            dots.forEach { pos ->
                drawCircle(
                    color = Color(0xFFC9A84C),
                    radius = 2.dp.toPx(),
                    center = Offset(size.width * pos, midY)
                )
            }
        }
    }
}

// ── أعمدة المسجد على الجانبين ─────────────────────────────────────────────────
@Composable
fun MosqueColumns() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // عمود يسار
        Canvas(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight()
        ) {
            val colW = size.width
            // خطوط العمود
            for (i in 0..20) {
                val y = size.height * i / 20f
                drawLine(
                    color = Color(0xFF8B4513).copy(alpha = 0.5f),
                    start = Offset(0f, y),
                    end = Offset(colW, y),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
            drawRect(
                color = Color(0xFF8B4513),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // عمود يمين
        Canvas(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight()
        ) {
            val colW = size.width
            for (i in 0..20) {
                val y = size.height * i / 20f
                drawLine(
                    color = Color(0xFF8B4513).copy(alpha = 0.5f),
                    start = Offset(0f, y),
                    end = Offset(colW, y),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
            drawRect(
                color = Color(0xFF8B4513),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

// ── زخرفة سفلية ───────────────────────────────────────────────────────────────
@Composable
fun BottomIslamicDecoration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MosqueBrown),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val midY = size.height / 2f
            // نمط هندسي متكرر
            val step = 20.dp.toPx()
            var x = step / 2
            while (x < size.width) {
                drawCircle(
                    color = Color(0xFFC9A84C),
                    radius = 3.dp.toPx(),
                    center = Offset(x, midY)
                )
                drawLine(
                    color = Color(0xFFC9A84C).copy(alpha = 0.5f),
                    start = Offset(x - step / 2, midY),
                    end = Offset(x + step / 2, midY),
                    strokeWidth = 1.dp.toPx()
                )
                x += step
            }
        }
    }
}

fun openXiaomiAutoStart(context: Context) {
    try {
        val intent = Intent().apply {
            component = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
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
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
