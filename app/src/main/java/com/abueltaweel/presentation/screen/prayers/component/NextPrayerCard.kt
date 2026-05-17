package com.abueltaweel.presentation.screen.prayers.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abueltaweel.R
import com.abueltaweel.presentation.base.LocalAppLocale
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.base.toLocalizedDigits
import com.abueltaweel.presentation.screen.prayers.FullPrayerTimesUiState
import com.abueltaweel.presentation.screen.prayers.MosqueBrown
import com.abueltaweel.presentation.screen.prayers.MosqueCreamy
import com.abueltaweel.presentation.screen.prayers.MosqueGold
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun NextPrayerCard(
    state: FullPrayerTimesUiState,
    countdownTime: FullPrayerTimesUiState.TimeUiState,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLocale.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFF3D0))
            .border(2.dp, MosqueGold, RoundedCornerShape(8.dp))
    ) {
        // زخرفة داخلية
        Canvas(modifier = Modifier.matchParentSize()) {
            // خط ذهبي داخلي
            drawRect(
                color = Color(0xFFC9A84C).copy(alpha = 0.2f),
                topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(
                    size.width - 12.dp.toPx(),
                    size.height - 12.dp.toPx()
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
            )
            // نقاط في الكورنرات
            val corners = listOf(
                Offset(12.dp.toPx(), 12.dp.toPx()),
                Offset(size.width - 12.dp.toPx(), 12.dp.toPx()),
                Offset(12.dp.toPx(), size.height - 12.dp.toPx()),
                Offset(size.width - 12.dp.toPx(), size.height - 12.dp.toPx())
            )
            corners.forEach { offset ->
                drawCircle(color = Color(0xFFC9A84C), radius = 3.dp.toPx(), center = offset)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // أيقونة المصلي
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MosqueBrown)
                    .border(1.dp, MosqueGold, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_prayer_man),
                    contentDescription = "prayer man icon",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(2.dp)
                )
            }

            // النص والوقت
            val nextPrayerText = if (state.nextPrayer.name != 0) {
                val prayerName = localizedString(state.nextPrayer.name)
                localizedString(R.string.next_prayer_in, prayerName)
            } else {
                localizedString(R.string.no_upcoming_prayer)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nextPrayerText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF5A3E00)
                )

                Spacer(Modifier.height(4.dp))

                // عداد الوقت بشكل LED
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0A0A0A))
                        .border(1.dp, Color(0xFF333300), RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val time = "${countdownTime.hours.toLocalizedDigits(language)}:" +
                            "${countdownTime.minutes.toLocalizedDigits(language)}:" +
                            countdownTime.seconds.toLocalizedDigits(language)
                    Text(
                        text = time,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFF2200),
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
