package com.abueltaweel.presentation.di

import com.abueltaweel.presentation.screen.AzkarDetails.AzkarDetailViewModel
import com.abueltaweel.presentation.screen.ReportBug.ReportBugViewModel
import com.abueltaweel.presentation.screen.SearchAyah.SearchAyahViewModel
import com.abueltaweel.presentation.screen.SurahAyat.SurahAyatViewModel
import com.abueltaweel.presentation.screen.azkar.AzkarViewModel
import com.abueltaweel.presentation.screen.audioazkar.AudioAzkarViewModel
import com.abueltaweel.presentation.screen.batteryOptimization.BatteryOptimizationViewModel
import com.abueltaweel.presentation.screen.bookmarks.BookMarkListViewModel
import com.abueltaweel.presentation.screen.calculation_method.CalculationMethodViewModel
import com.abueltaweel.presentation.screen.home.HomeViewModel
import com.abueltaweel.presentation.screen.location_permission.LocationViewModel
import com.abueltaweel.presentation.screen.madhab.MadhabViewModel
import com.abueltaweel.presentation.screen.maps.MapsViewModel
import com.abueltaweel.presentation.screen.prayers.FullPrayerTimesViewModel
import com.abueltaweel.presentation.screen.qiblah.QiblahViewModel
import com.abueltaweel.presentation.screen.quran.SurahListViewModel
import com.abueltaweel.presentation.screen.radio.player.AudioPlayerManager
import com.abueltaweel.presentation.screen.radio.player.PlayerController
import com.abueltaweel.presentation.screen.radio.RadioChannelsViewModel
import com.abueltaweel.presentation.screen.settings.SettingsViewModel
import com.abueltaweel.presentation.utils.AnalyticsHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::FullPrayerTimesViewModel)
    viewModelOf(::QiblahViewModel)
    viewModelOf(::LocationViewModel)
    viewModelOf(::MadhabViewModel)
    viewModelOf(::CalculationMethodViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MapsViewModel)
    viewModelOf(::AzkarViewModel)
    viewModelOf(::AzkarDetailViewModel)
    viewModelOf(::SurahListViewModel)
    viewModelOf(::SurahAyatViewModel)
    viewModelOf(::SearchAyahViewModel)
    viewModelOf(::ReportBugViewModel)
    viewModelOf(::BookMarkListViewModel)
    viewModelOf(::RadioChannelsViewModel)
    viewModelOf(::BatteryOptimizationViewModel)
    viewModelOf(::AudioAzkarViewModel)
    single<PlayerController> { AudioPlayerManager(androidContext()) }
     single { AnalyticsHelper(get()) }
}
