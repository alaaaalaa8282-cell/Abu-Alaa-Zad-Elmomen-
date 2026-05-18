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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.abueltaweel.R
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.screen.prayers.component.NextPrayerCardimport com.abueltaweel.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime

object EqamaOffsets {
    const val FAJR    = 25
    const val ZUHR    = 15
    const val ASR     = 15
    const val MAGHRIB = 12
    const val ISHA    = 20
}

private fun calcEqamaTime(azanTime: String, offsetMinutes: Int): String {
    return try {
        val parts   = azanTime.split(":")
        val hours   = parts[0].trim().toInt()
        val minutes = parts[1].trim().toInt()
        val total   = hours * 60 + minutes + offsetMinutes
        val h       = (total / 60) % 24
        val m       = total % 60
        "%02d:%02d".format(h, m)
    } catch (e: Exception) {
        "--:--"
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("BatteryLife")
@ExperimentalTime
@Composable
fun FullPrayerTimesViewScreen(
    navController: NavController,
    viewModel: FullPrayerTimesViewModel = koinViewModel()
) {
    val state         by viewModel.screenState.collectAsStateWithLifecycle()
    val countdownTime by viewModel.countdownTime.collectAsStateWithLifecycle()
    val context       = LocalContext.current
    
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted)
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
    }
    
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            FullPrayerTimesEffect.RequestExactAlarm ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))            FullPrayerTimesEffect.RequestNotificationPermission ->
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            FullPrayerTimesEffect.RequestIgnoreBatteryOptimization ->
                context.startActivity(
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                )
            FullPrayerTimesEffect.RequestXiaomiAutoStart -> openXiaomiAutoStart(context)
            FullPrayerTimesEffect.NavigateBack           -> navController.popBackStack()
        }
    }
    
    LaunchedEffect(Unit) { viewModel.onScreenOpened() }
    
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
                            text          = localizedString(R.string.prayer_times),
                            fontSize      = 18.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = MosqueColors.Gold,
                            letterSpacing = 2.sp
                        )
                    }                }
                item { IslamicDivider() }
                item {
                    NextPrayerCard(
                        state         = state,
                        countdownTime = countdownTime,
                    )
                }
                item { IslamicDivider() }
                item { PrayerTableHeader() }
                
                items(state.prayers) { prayer ->
                    val arabicName = when (prayer.name) {
                        Prayer.PrayerName.FAJR    -> "الفجر"
                        Prayer.PrayerName.ZUHR    -> "الظهر"
                        Prayer.PrayerName.ASR     -> "العصر"
                        Prayer.PrayerName.MAGHRIB -> "المغرب"
                        Prayer.PrayerName.ISHA    -> "العشاء"
                    }
                    val eqamaOffset = when (prayer.name) {
                        Prayer.PrayerName.FAJR    -> EqamaOffsets.FAJR
                        Prayer.PrayerName.ZUHR    -> EqamaOffsets.ZUHR
                        Prayer.PrayerName.ASR     -> EqamaOffsets.ASR
                        Prayer.PrayerName.MAGHRIB -> EqamaOffsets.MAGHRIB
                        Prayer.PrayerName.ISHA    -> EqamaOffsets.ISHA
                    }
                    
                    PrayerRow(
                        prayerName            = arabicName,
                        azanTime              = prayer.time.time,
                        eqamaTime             = calcEqamaTime(prayer.time.time, eqamaOffset),
                        isAm                  = prayer.time.isAm,
                        isNextPrayer          = prayer.isUpComing,
                        isNotificationEnabled = prayer.isNotificationEnabled,
                        onNotificationClick   = { _, enabled ->
                            // FIX: Pass the Enum 'prayer.name' instead of the String 'name'
                            viewModel.onClickEnablePrayer(prayer.name, enabled)
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

@Composable
private fun PrayerTableHeader() {    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MosqueColors.Brown.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = "EQAMA\nالإقامة",
            modifier   = Modifier.weight(1f),
            textAlign  = TextAlign.Center,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            color      = MosqueColors.Gold,
            lineHeight = 15.sp
        )
        Text(
            text       = "الصلاة",
            modifier   = Modifier.weight(1.2f),
            textAlign  = TextAlign.Center,
            fontSize   = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = MosqueColors.Creamy
        )
        Text(
            text       = "AZAN\nالأذان",
            modifier   = Modifier.weight(1f),
            textAlign  = TextAlign.Center,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            color      = MosqueColors.Gold,
            lineHeight = 15.sp
        )
    }
}

@Composable
private fun PrayerRow(
    prayerName           : String,
    azanTime             : String,
    eqamaTime            : String,
    isAm                 : Boolean,
    isNextPrayer         : Boolean,
    isNotificationEnabled: Boolean,
    onNotificationClick  : (String, Boolean) -> Unit
) {
    val amPmLabel = if (isAm) "صباحاً" else "مساءً"
    Row(
        modifier = Modifier
            .fillMaxWidth()            .background(if (isNextPrayer) MosqueColors.Brown.copy(alpha = 0.18f) else Color.Transparent)
            .then(if (isNextPrayer) Modifier.border(1.5.dp, MosqueColors.Gold) else Modifier)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier            = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LedDisplay(time = eqamaTime, color = MosqueColors.LedGreen)
            Spacer(Modifier.height(2.dp))
            Text(text = amPmLabel, fontSize = 9.sp, color = MosqueColors.Brown, textAlign = TextAlign.Center)
        }
        Column(
            modifier            = Modifier.weight(1.2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MosqueColors.Gold.copy(alpha = 0.25f))
                    .border(1.dp, MosqueColors.Gold, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = prayerName,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = MosqueColors.Brown,
                    textAlign  = TextAlign.Center
                )
            }
            if (isNextPrayer) {
                Spacer(Modifier.height(2.dp))
                Text("", fontSize = 12.sp, color = MosqueColors.Gold)
            }
            Spacer(Modifier.height(4.dp))
            IconButton(
                onClick  = { onNotificationClick(prayerName, !isNotificationEnabled) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter            = painterResource(
                        if (isNotificationEnabled) R.drawable.ic_volume_on else R.drawable.ic_volume_off
                    ),
                    contentDescription = null,
                    tint               = if (isNotificationEnabled) MosqueColors.Gold
                    else MosqueColors.Brown.copy(alpha = 0.4f),
                    modifier           = Modifier.size(20.dp)                )
            }
        }
        Column(
            modifier            = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LedDisplay(time = azanTime, color = MosqueColors.LedRed)
            Spacer(Modifier.height(2.dp))
            Text(text = amPmLabel, fontSize = 9.sp, color = MosqueColors.Brown, textAlign = TextAlign.Center)
        }
    }
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MosqueColors.Gold.copy(alpha = 0.25f)))
}

@Composable
private fun LedDisplay(time: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MosqueColors.LedBg)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text          = time,
            fontSize      = 20.sp,
            fontWeight    = FontWeight.Bold,
            color         = color,
            letterSpacing = 2.sp,
            fontFamily    = FontFamily.Monospace
        )
    }
}

@Composable
private fun MosqueArchHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MosqueColors.Brown)
    ) {
        CornerDecoration(modifier = Modifier.align(Alignment.TopStart))
        CornerDecoration(modifier = Modifier.align(Alignment.TopEnd).graphicsLayer { scaleX = -1f })
        Canvas(modifier = Modifier.fillMaxSize()) { drawMosqueArch(size) }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.62f)                .align(Alignment.Center)
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "اللهم اغفر للمرحوم\nمحمد عبد العظيم طرفايه\nوارحمه وعافه واعف عنه\nواجعل قبره روضة من رياض الجنة\nواغفر له ذنوبه وزد حسناته",
                fontSize   = 11.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF3D1A00),
                textAlign  = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

private fun DrawScope.drawMosqueArch(size: Size) {
    val archWidth  = size.width * 0.65f
    val archLeft   = (size.width - archWidth) / 2f
    val archRadius = archWidth / 2f
    drawRect(color = Color(0xFFF5E6C0), topLeft = Offset(archLeft, size.height * 0.45f), size = Size(archWidth, size.height * 0.6f))
    drawArc(color = Color(0xFFF5E6C0), startAngle = 180f, sweepAngle = 180f, useCenter = true, topLeft = Offset(archLeft, size.height * 0.45f - archRadius), size = Size(archWidth, archWidth))
    drawArc(color = Color(0xFFC9A84C), startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(archLeft, size.height * 0.45f - archRadius), size = Size(archWidth, archWidth), style = Stroke(width = 3.dp.toPx()))
    drawLine(color = Color(0xFFC9A84C), start = Offset(archLeft, size.height * 0.45f), end = Offset(archLeft, size.height), strokeWidth = 3.dp.toPx())
    drawLine(color = Color(0xFFC9A84C), start = Offset(archLeft + archWidth, size.height * 0.45f), end = Offset(archLeft + archWidth, size.height), strokeWidth = 3.dp.toPx())
    val centerX = size.width / 2f
    val centerY = size.height * 0.45f
    for (i in 0..8) {
        val angle = Math.PI * i / 8.0
        drawCircle(color = Color(0xFFC9A84C), radius = 4.dp.toPx(), center = Offset((centerX - archRadius * Math.cos(angle)).toFloat(), (centerY - archRadius * Math.sin(angle)).toFloat()))
    }
}

@Composable
private fun CornerDecoration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(70.dp)) {
        val s = size.minDimension
        for (i in 0..2) {
            val inset = i * 6.dp.toPx()
            drawRect(color = Color(0xFFC9A84C).copy(alpha = 1f - i * 0.25f), topLeft = Offset(inset, inset), size = Size(s - inset * 2, s - inset * 2), style = Stroke(width = 1.5.dp.toPx()))
        }
        drawLine(color = Color(0xFFC9A84C), start = Offset(0f, 0f), end = Offset(s * 0.5f, 0f), strokeWidth = 2.dp.toPx())
        drawLine(color = Color(0xFFC9A84C), start = Offset(0f, 0f), end = Offset(0f, s * 0.5f), strokeWidth = 2.dp.toPx())
        drawCircle(color = Color(0xFFC9A84C), radius = 6.dp.toPx(), center = Offset(s * 0.25f, s * 0.25f), style = Stroke(width = 1.5.dp.toPx()))
        drawCircle(color = Color(0xFFC9A84C), radius = 2.dp.toPx(), center = Offset(s * 0.25f, s * 0.25f))
    }
}

@Composable
private fun IslamicDivider() {    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            val midY = size.height / 2f
            val midX = size.width / 2f
            drawLine(color = Color(0xFFC9A84C), start = Offset(0f, midY), end = Offset(size.width, midY), strokeWidth = 1.dp.toPx())
            val starR = 5.dp.toPx()
            for (i in 0..3) {
                val angle = Math.PI * i / 4.0
                drawLine(color = Color(0xFFC9A84C), start = Offset((midX - starR * Math.cos(angle)).toFloat(), (midY - starR * Math.sin(angle)).toFloat()), end = Offset((midX + starR * Math.cos(angle)).toFloat(), (midY + starR * Math.sin(angle)).toFloat()), strokeWidth = 1.5.dp.toPx())
            }
            listOf(0.2f, 0.3f, 0.7f, 0.8f).forEach { pos ->
                drawCircle(color = Color(0xFFC9A84C), radius = 2.dp.toPx(), center = Offset(size.width * pos, midY))
            }
        }
    }
}

@Composable
private fun MosqueColumns() {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
        repeat(2) {
            Canvas(modifier = Modifier.width(8.dp).fillMaxHeight()) {
                for (i in 0..20) {
                    drawLine(color = Color(0xFF8B4513).copy(alpha = 0.5f), start = Offset(0f, size.height * i / 20f), end = Offset(size.width, size.height * i / 20f), strokeWidth = 0.5.dp.toPx())
                }
                drawRect(color = Color(0xFF8B4513), style = Stroke(width = 1.dp.toPx()))
            }
        }
    }
}

@Composable
private fun BottomIslamicDecoration() {
    Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(MosqueColors.Brown), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val midY = size.height / 2f
            val step = 20.dp.toPx()
            var x    = step / 2
            while (x < size.width) {
                drawCircle(color = Color(0xFFC9A84C), radius = 3.dp.toPx(), center = Offset(x, midY))
                drawLine(color = Color(0xFFC9A84C).copy(alpha = 0.5f), start = Offset(x - step / 2, midY), end = Offset(x + step / 2, midY), strokeWidth = 1.dp.toPx())
                x += step
            }
        }
    }
}

fun openXiaomiAutoStart(context: Context) {
    try {
        context.startActivity(Intent().apply {            component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
        })
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
        context.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply { data = "package:${context.packageName}".toUri() })
        return
    }
    if (Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)) {
        try {
            context.startActivity(Intent().apply {
                component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            })
        } catch (_: Exception) {}
    }
}
