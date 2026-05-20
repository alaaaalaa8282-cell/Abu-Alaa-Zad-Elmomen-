package com.abueltaweel.presentation.screen.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.abueltaweel.R
import com.abueltaweel.design_system.theme.Theme
import com.abueltaweel.presentation.base.LocalAppLocale
import com.abueltaweel.presentation.navigation.Route
import com.abueltaweel.presentation.screen.home.component.ContinueToTilawah
import com.abueltaweel.presentation.screen.home.component.FeaturesSection
import com.abueltaweel.presentation.screen.home.component.HomeAppBar
import com.abueltaweel.presentation.screen.home.component.PrayerTimesCard
import com.abueltaweel.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel
import com.abueltaweel.presentation.screen.home.component.WeatherWidget

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    RequestNotificationPermission()
    val language = LocalAppLocale.current
    LaunchedEffect(Unit) {
        viewModel.getHijriDate(language)
    }
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            HomeEffect.NavigateToFullPrayersDetails -> {
                navController.navigate(Route.FullPrayerTimeView)
            }
            HomeEffect.NavigateToCalibrateDevice -> {
                navController.navigate(Route.CalibrateDevice)
            }
            HomeEffect.NavigateToSettings -> {
                navController.navigate(Route.SettingsScreen)
            }
            HomeEffect.NavigateToQuran -> {
                navController.navigate(Route.SurahListScreen)
            }
            HomeEffect.NavigateToTilawah -> {
                // TODO
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // ─── صورة الوالد رحمه الله ───
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.father_photo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // gradient overlay عشان النص يبان
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xCC000000)
                                )
                            )
                        )
                )
                Text(
                    text = "رحمه الله ووسع له في قبره",
                    color = Color(0xFFE2B96F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }
        }

        item {
            HomeAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                state = state,
            )
        }
     // ─── بطاقة الطقس ─────────────────────────────────────────────────────
 item {
    WeatherWidget(
        modifier = Modifier.padding(horizontal = 16.dp),
        lat = state.location.latitude,
        lon = state.location.longitude
    )
}
       
        item {
            PrayerTimesCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                prayerTimesUiState = state,
            )
        }
        item {
            ContinueToTilawah(
                onClick = {
                    navController.navigate(
                        Route.SurahAyatScreen(
                            surahId = state.lastTilawahUi.surahId,
                            arabicName = state.lastTilawahUi.nameArabic,
                            englishName = state.lastTilawahUi.nameEnglish,
                            targetAyahId = state.lastTilawahUi.ayahId
                        )
                    )
                },
                surahUiState = state.lastTilawahUi,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            )
        }
        item { FeaturesSection(homeInteractionListener = viewModel) }
    }
}

@Composable
fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) {}
        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
