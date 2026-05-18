```kotlin
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.presentation.screen.prayers.FullPrayerTimesUiState
import com.abueltaweel.presentation.screen.prayers.MosqueColors

@Composable
fun NextPrayerCard(
    state: FullPrayerTimesUiState,
    countdownTime: FullPrayerTimesUiState.TimeUiState,
    modifier: Modifier = Modifier
) {
    val nextPrayer = state.nextPrayer
    // تم تحديث المقارنة لتكون مع نوع Enum مباشرة بدلاً من المقارنة مع أرقام Int
    val prayerArabicName = when (nextPrayer.name) {
        Prayer.PrayerName.FAJR -> "الفجر"
        Prayer.PrayerName.ZUHR -> "الظهر"
        Prayer.PrayerName.ASR -> "العصر"
        Prayer.PrayerName.MAGHRIB -> "المغرب"
        Prayer.PrayerName.ISHA -> "العشاء"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MosqueColors.Brown)
            .border(2.dp, MosqueColors.Gold, RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "الصلاة القادمة: $prayerArabicName",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MosqueColors.Creamy,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "الوقت المتبقي للأذان",
                fontSize = 12.sp,
                color = MosqueColors.Gold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeBlock(time = countdownTime.hours, label = "ساعة")
                Text(text = ":", color = MosqueColors.Gold, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                TimeBlock(time = countdownTime.minutes, label = "دقيقة")
                Text(text = ":", color = MosqueColors.Gold, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                TimeBlock(time = countdownTime.seconds, label = "ثانية")
            }
        }
    }
}

@Composable
private fun TimeBlock(time: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black)
                .border(1.dp, MosqueColors.Gold, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = time,
                color = MosqueColors.LedRed,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = MosqueColors.Creamy.copy(alpha = 0.8f),
            fontSize = 9.sp
        )
    }
}

```
