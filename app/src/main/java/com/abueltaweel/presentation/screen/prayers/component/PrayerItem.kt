package com.abueltaweel.presentation.screen.prayers.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abueltaweel.R
import com.abueltaweel.domain.entity.prayer.Prayer
import com.abueltaweel.presentation.base.LocalAppLocale
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.base.toLocalizedDigits
import com.abueltaweel.presentation.screen.prayers.MosqueColors

@Composable
fun PrayerItem(
    prayerNameResource: Int,
    prayerTime: String,
    isAm: Boolean,
    isNextPrayer: Boolean,
    isNotificationEnabled: Boolean = false,
    onNotificationClick: (Prayer.PrayerName, Boolean) -> Unit,
) {
    val language = LocalAppLocale.current
    val amPm = if (isAm) localizedString(R.string.am) else localizedString(R.string.pm)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val rowBg       = if (isNextPrayer) Color(0xFFFFF3D0) else Color(0xFFF5E6C8)
    val borderColor = if (isNextPrayer) MosqueColors.Gold else Color(0xFF8B6914)
    val nameBg      = if (isNextPrayer) MosqueColors.Gold else Color(0xFFB8860B)
    val borderWidth = if (isNextPrayer) 2.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(rowBg)
                .border(borderWidth, borderColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // اسم الصلاة
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(nameBg)
                    .border(1.dp, Color(0xFF6B2A0A), RoundedCornerShape(6.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val r = 2.dp.toPx()
                    listOf(
                        Offset(r * 2, r * 2),
                        Offset(size.width - r * 2, r * 2),
                        Offset(r * 2, size.height - r * 2),
                        Offset(size.width - r * 2, size.height - r * 2)
                    ).forEach {
                        drawCircle(color = Color(0xFF6B2A0A).copy(alpha = 0.5f), radius = r, center = it)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isNextPrayer) {
                        Icon(
                            painter = painterResource(R.drawable.ic_next_prayer_arrow),
                            tint = Color(0xFF1A0A00),
                            contentDescription = "",
                            modifier = Modifier
                                .size(14.dp)
                                .padding(end = 4.dp)
                                .graphicsLayer { scaleX = if (isRtl) -1f else 1f }
                        )
                    }
                    Text(
                        text = localizedString(prayerNameResource),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A0A00)
                    )
                }
            }

            // الوقت
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = amPm,
                    fontSize = 11.sp,
                    color = Color(0xFF5A3E00),
                    modifier = Modifier.padding(end = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0A0A0A))
                        .border(1.dp, Color(0xFF333300), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = prayerTime.toLocalizedDigits(language),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFF2200),
                        letterSpacing = 2.sp
                    )
                }

                Spacer(Modifier.width(8.dp))

                AnimatedContent(
                    targetState = isNotificationEnabled,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    label = "NotificationIconAnimation"
                ) { _ ->
                    Icon(
                        painter = painterResource(
                            if (isNotificationEnabled) R.drawable.ic_volume_on
                            else R.drawable.ic_volume_off
                        ),
                        contentDescription = null,
                        tint = if (isNotificationEnabled) MosqueColors.Gold else Color(0xFF8B6914),
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2A1800))
                            .padding(4.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onNotificationClick(prayerNameResource.toPrayerName(), !isNotificationEnabled)
                            }
                    )
                }
            }
        }

        // شريط ذهبي جانبي للصلاة القادمة
        if (isNextPrayer) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                    .background(MosqueColors.Gold)
            )
        }
    }
}

fun Int.toPrayerName(): Prayer.PrayerName {
    return when (this) {
        R.string.fajr    -> Prayer.PrayerName.FAJR
        R.string.dhuhr   -> Prayer.PrayerName.ZUHR
        R.string.asr     -> Prayer.PrayerName.ASR
        R.string.maghrib -> Prayer.PrayerName.MAGHRIB
        R.string.isha    -> Prayer.PrayerName.ISHA
        else             -> Prayer.PrayerName.FAJR
    }
}
