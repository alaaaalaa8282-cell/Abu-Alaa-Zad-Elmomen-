package com.abueltaweel.presentation.screen.prayers.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abueltaweel.R
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.presentation.base.LocalAppLocale
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.base.toLocalizedDigits
import com.abueltaweel.presentation.screen.prayers.FullPrayerTimesUiState
import com.abueltaweel.presentation.screen.prayers.MosqueColors
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun PrayerTimesCardFull(
    state: FullPrayerTimesUiState,
    modifier: Modifier = Modifier
) {
    if (state.prayers.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFF3D0))
            .border(2.dp, MosqueColors.Gold, RoundedCornerShape(10.dp))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                color   = MosqueColors.Gold.copy(alpha = 0.2f),
                topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                size    = Size(size.width - 12.dp.toPx(), size.height - 12.dp.toPx()),
                style   = Stroke(width = 1.dp.toPx())
            )
            listOf(
                Offset(12.dp.toPx(), 12.dp.toPx()),
                Offset(size.width - 12.dp.toPx(), 12.dp.toPx()),
                Offset(12.dp.toPx(), size.height - 12.dp.toPx()),
                Offset(size.width - 12.dp.toPx(), size.height - 12.dp.toPx())
            ).forEach { drawCircle(color = MosqueColors.Gold, radius = 3.dp.toPx(), center = it) }
        }

        LazyRow(
            modifier              = Modifier.fillMaxSize().padding(vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            contentPadding        = PaddingValues(horizontal = 12.dp),
        ) {
            itemsIndexed(state.prayers) { _, prayer ->
                PrayerTimeItem(prayer = prayer, isNextPrayer = prayer.isUpComing)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun PrayerTimeItem(
    prayer      : FullPrayerTimesUiState.PrayerUiState,
    isNextPrayer: Boolean,
    modifier    : Modifier = Modifier,
) {
    val language = LocalAppLocale.current

    val prayerArabicName = when (prayer.name) {
        Prayer.PrayerName.FAJR    -> localizedString(R.string.fajr)
        Prayer.PrayerName.ZUHR    -> localizedString(R.string.dhuhr)
        Prayer.PrayerName.ASR     -> localizedString(R.string.asr)
        Prayer.PrayerName.MAGHRIB -> localizedString(R.string.maghrib)
        Prayer.PrayerName.ISHA    -> localizedString(R.string.isha)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .padding(bottom = if (isNextPrayer) 16.dp else 8.dp)
                .padding(horizontal = 6.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = prayerArabicName,
                fontSize   = 12.sp,
                fontWeight = if (isNextPrayer) FontWeight.Bold else FontWeight.Normal,
                color      = if (isNextPrayer) MosqueColors.Brown else Color(0xFF5A3E00),
            )
            Text(
                text       = prayer.time.time.toLocalizedDigits(language),
                fontSize   = if (isNextPrayer) 20.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color      = if (isNextPrayer) MosqueColors.Brown else Color(0xFF8B6914),
            )
            Text(
                text     = if (prayer.time.isAm) localizedString(R.string.am) else localizedString(R.string.pm),
                fontSize = 11.sp,
                color    = Color(0xFF8B6914),
            )
        }

        if (isNextPrayer) {
            Icon(
                painter           = painterResource(R.drawable.ic_triangle_down),
                contentDescription = null,
                tint               = MosqueColors.Gold,
                modifier           = Modifier.align(Alignment.BottomCenter).size(14.dp),
            )
        }
    }
}
