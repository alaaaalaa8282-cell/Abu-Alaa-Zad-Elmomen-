package com.abueltaweel.presentation.screen.prayers.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.graphics.Color
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

    val bgColor = if (isNextPrayer) Color(0xFF1A3A2A) else Color(0xFF0D1F1A)
    val borderColor = if (isNextPrayer) Color(0xFFC9A84C) else Color(0xFF1E3A2A)
    val timeColor = if (isNextPrayer) Color(0xFFFF4444) else Color(0xFF00CC66)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // اسم الصلاة
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isNextPrayer) {
                Icon(
                    painter = painterResource(R.drawable.ic_next_prayer_arrow),
                    tint = Color(0xFFC9A84C),
                    contentDescription = "",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                        .graphicsLayer { scaleX = if (isRtl) -1f else 1f }
                )
            }
            Text(
                text = localizedString(prayerNameResource),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isNextPrayer) Color(0xFFC9A84C) else Color(0xFF8BAF8B),
                modifier = Modifier.padding(start = if (isNextPrayer) 4.dp else 0.dp)
            )
        }

        // الوقت بشكل ديجيتال
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = prayerTime.toLocalizedDigits(language),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = timeColor
            )
            Text(
                text = " $amPm",
                fontSize = 12.sp,
                color = Color(0xFF8BAF8B),
                modifier = Modifier.padding(start = 4.dp)
            )

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
                    tint = if (isNotificationEnabled) Color(0xFFC9A84C) else Color(0xFF4A6A4A),
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E3A2A))
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
}

fun Int.toPrayerName(): Prayer.PrayerName {
    return when (this) {
        R.string.fajr -> Prayer.PrayerName.FAJR
        R.string.dhuhr -> Prayer.PrayerName.ZUHR
        R.string.asr -> Prayer.PrayerName.ASR
        R.string.maghrib -> Prayer.PrayerName.MAGHRIB
        R.string.isha -> Prayer.PrayerName.ISHA
        else -> Prayer.PrayerName.FAJR
    }
}
