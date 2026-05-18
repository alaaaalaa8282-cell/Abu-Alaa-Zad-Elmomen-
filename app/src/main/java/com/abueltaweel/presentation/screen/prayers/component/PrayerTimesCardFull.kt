Enterpackage com.abueltaweel.presentation.screen.prayers.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abueltaweel.R
import com.abueltaweel.design_system.theme.Theme
import com.abueltaweel.presentation.base.LocalAppLocale
import com.abueltaweel.presentation.base.LocalIsDarkTheme
import com.abueltaweel.presentation.base.localizedString
import com.abueltaweel.presentation.base.toLocalizedDigits
import com.abueltaweel.presentation.screen.prayers.FullPrayerTimesUiState
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun PrayerTimesCardFull(
    state: FullPrayerTimesUiState,
    modifier: Modifier = Modifier
) {
    if (state.prayers.isEmpty()) return
    val isDark = LocalIsDarkTheme.current

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .aspectRatio(2.65f)
                .fillMaxSize()
                .align(Alignment.BottomCenter)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.color.surfaces.surfaceLow)
        )

        val image = if (isDark) R.drawable.ic_mosque_dark_bg else R.drawable.ic_mosque_light_bg
        Image(
            painter = painterResource(image),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.35f)
                .aspectRatio(1.52f),
            contentScale = ContentScale.Fit,
        )

        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly,
            contentPadding = PaddingValues(horizontal = 12.dp),
        ) {
            itemsIndexed(state.prayers) { _, prayer ->
                PrayerTimeItem(
                    prayer = prayer,
                    isNextPrayer = prayer.isUpComing
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.mosque_column),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .padding(start = 22.dp),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun PrayerTimeItem(
    prayer: FullPrayerTimesUiState.PrayerUiState,
    isNextPrayer: Boolean,
    modifier: Modifier = Modifier,
) {
    val language = LocalAppLocale.current

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .padding(horizontal = 2.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = localizedString(prayer.name),
                color = Theme.color.secondary.secondaryText,
                style = Theme.textStyle.label.small,
            )
            Text(
                text = prayer.time.time.toLocalizedDigits(language),
                color = Theme.color.secondary.secondary,
                style = Theme.textStyle.title.medium,
            )
            Text(
                text = if (prayer.time.isAm) localizedString(R.string.am)
                else localizedString(R.string.pm),
                style = Theme.textStyle.label.small,
                color = Theme.color.secondary.secondaryText,
            )
        }

        if (isNextPrayer) {
            Icon(
                painter = painterResource(R.drawable.ic_triangle_down),
                contentDescription = null,
                tint = Theme.color.secondary.secondaryText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(16.dp),
            )
        }
    }
}
