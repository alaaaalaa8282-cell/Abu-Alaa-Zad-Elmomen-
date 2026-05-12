package com.abueltaweel.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable data object SplashScreen : Route()          // ← جديد
    @Serializable data object AppRoute : Route()
    @Serializable data object MadhabScreen : Route()
    @Serializable data object SurahListScreen : Route()
    @Serializable data object CalibrateDevice : Route()
    @Serializable data object FullPrayerTimeView : Route()
    @Serializable data object QiblahScreen : Route()
    @Serializable data object LocationPermissionScreen : Route()
    @Serializable data object CalculationMethodScreen : Route()
    @Serializable data object SettingsScreen : Route()
    @Serializable data object MapsScreen : Route()
    @Serializable data object AzkarScreen : Route()
    @Serializable data object ReportBugScreen : Route()
    @Serializable data object BookmarksListScreen : Route()
    @Serializable data object BatteryOptimizationScreen : Route()

    @Serializable data class AzkarDetailScreen(val title: String) : Route()
    @Serializable data class SurahAyatScreen(
        val surahId: Int,
        val arabicName: String,
        val englishName: String
    ) : Route()
    @Serializable data class SearchAyahScreen(
        val type: String,
        val surahId: Int,
        val surahName: String
    ) : Route()
}
