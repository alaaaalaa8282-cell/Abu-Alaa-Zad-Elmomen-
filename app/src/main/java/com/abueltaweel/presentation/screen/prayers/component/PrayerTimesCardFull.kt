@file:OptIn(ExperimentalTime::class)

package com.abueltaweel.presentation.screen.prayers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.presentation.screen.prayers.FullPrayerTimesUiState
import com.abueltaweel.presentation.screen.prayers.MosqueColors
import kotlin.time.ExperimentalTime

@Composable
fun PrayerTimesCardFull(
    prayer: FullPrayerTimesUiState.PrayerUiState,
    modifier: Modifier = Modifier
) {
    val prayerArabicName = when (prayer.name) {
        Prayer.PrayerName.FAJR -> "الفجر"
        Prayer.PrayerName.ZUHR -> "الظهر"
        Prayer.PrayerName.ASR -> "العصر"
        Prayer.PrayerName.MAGHRIB -> "المغرب"
        Prayer.PrayerName.ISHA -> "العشاء"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MosqueColors.Creamy)
            .border(1.dp, MosqueColors.Gold, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = prayerArabicName,
                color = MosqueColors.Brown,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = prayer.time.time,
                color = MosqueColors.Brown,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
