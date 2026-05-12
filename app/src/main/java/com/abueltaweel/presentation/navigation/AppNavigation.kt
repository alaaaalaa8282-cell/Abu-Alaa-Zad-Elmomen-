package com.abueltaweel.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.abueltaweel.domain.repository.settings.SettingsRepository
import com.abueltaweel.presentation.screen.AzkarDetails.AzkarDetailScreen
import com.abueltaweel.presentation.screen.ReportBug.ReportBugScreen
import com.abueltaweel.presentation.screen.SearchAyah.SearchAyahScreen
import com.abueltaweel.presentation.screen.SurahAyat.SurahAyatScreen
import com.abueltaweel.presentation.screen.azkar.AzkarScreen
import com.abueltaweel.presentation.screen.batteryOptimization.BatteryOptimizationScreen
import com.abueltaweel.presentation.screen.bookmarks.BookmarksListScreen
import com.abueltaweel.presentation.screen.calculation_method.CalculationMethodScreen
import com.abueltaweel.presentation.screen.calibrate_device.Figure8CalibrationScreen
import com.abueltaweel.presentation.screen.location_permission.LocationPermissionScreen
import com.abueltaweel.presentation.screen.madhab.MadhabScreen
import com.abueltaweel.presentation.screen.maps.MapsScreen
import com.abueltaweel.presentation.screen.prayers.FullPrayerTimesViewScreen
import com.abueltaweel.presentation.screen.qiblah.QiblahScreen
import com.abueltaweel.presentation.screen.quran.SurahListScreen
import com.abueltaweel.presentation.screen.radio.RadioScreen
import com.abueltaweel.presentation.screen.settings.SettingsScreen
import com.abueltaweel.presentation.screen.splash.SplashScreen  // ← جديد

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun AppNavigation(settingsRepository: SettingsRepository) {
    val navController = rememberNavController()

    val onboardingComplete by settingsRepository
        .observeOnboardingComplete()
        .collectAsState(initial = null)

    if (onboardingComplete == null) return

    val afterSplashDestination =
        if (onboardingComplete == true) Route.AppRoute
        else Route.MadhabScreen

    NavHost(
        navController = navController,
        startDestination = Route.SplashScreen.route  // ← الـ Splash أول حاجة
    ) {
        // ─── Splash ───
        composable(Route.SplashScreen.route) {
            SplashScreen(
                navController = navController,
                startDestination = afterSplashDestination
            )
        }

        composable<Route.SurahListScreen> { SurahListScreen(navController) }
        composable<Route.AppRoute> {
            MainContainer(rootNavController = navController)
        }
        composable<Route.CalibrateDevice> { Figure8CalibrationScreen(navController) }
        composable<Route.FullPrayerTimeView> { FullPrayerTimesViewScreen(navController) }
        composable<Route.QiblahScreen> { QiblahScreen(navController) }
        composable<Route.LocationPermissionScreen> { LocationPermissionScreen(navController) }
        composable<Route.MadhabScreen> { MadhabScreen(navController) }
        composable<Route.CalculationMethodScreen> { CalculationMethodScreen(navController) }
        composable<Route.SettingsScreen> { SettingsScreen(navController) }
        composable<Route.MapsScreen> { MapsScreen(navController) }
        composable<Route.AzkarScreen> { AzkarScreen(navController) }
        composable<Route.AzkarDetailScreen> { entry ->
            val args = entry.toRoute<Route.AzkarDetailScreen>()
            AzkarDetailScreen(
                title = args.title,
                navController = navController
            )
        }
        composable<Route.SurahAyatScreen> { entry ->
            val surah = entry.toRoute<Route.SurahAyatScreen>()
            SurahAyatScreen(
                navController = navController,
                surahId = surah.surahId,
                arabicName = surah.arabicName,
                englishName = surah.englishName
            )
        }
        composable<Route.SearchAyahScreen> { entry ->
            val args = entry.toRoute<Route.SearchAyahScreen>()
            SearchAyahScreen(
                navController = navController,
                searchType = args.type,
                surahId = args.surahId,
                surahName = args.surahName
            )
        }
        composable<Route.ReportBugScreen> {
            ReportBugScreen(navController = navController)
        }
        composable<Route.BookmarksListScreen> {
            BookmarksListScreen(navController = navController)
        }
        composable<Route.BatteryOptimizationScreen> {
            BatteryOptimizationScreen(navController = navController)
        }
    }
}
