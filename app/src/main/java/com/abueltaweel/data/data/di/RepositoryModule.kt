package com.abueltaweel.data.di

import com.abueltaweel.data.azkar.repository.AzkarRepositoryImpl
import com.abueltaweel.data.bugReport.repository.BugReportRepositoryImpl
import com.abueltaweel.data.location.LocationRepositoryImpl
import com.abueltaweel.data.prayer.repository.PrayerAlarmRepositoryImpl
import com.abueltaweel.data.prayer.repository.PrayerNotificationsRepositoryImpl
import com.abueltaweel.data.prayer.repository.PrayerRepositoryImpl
import com.abueltaweel.data.qiblah.QiblahRepositoryImpl
import com.abueltaweel.data.quran.repository.BookmarkRepositoryImpl
import com.abueltaweel.data.quran.repository.QuranRepositoryImpl
import com.abueltaweel.data.quran.repository.ReadingProgressRepositoryImpl
import com.abueltaweel.data.radio.repository.RadioRepositoryImpl
import com.abueltaweel.data.settings.repositiory.BatteryOptimizationRepositoryImpl
import com.abueltaweel.data.settings.repositiory.SettingsRepositoryImpl
import com.abueltaweel.data.util.network.NetworkConnectionRepositoryImpl
import com.abueltaweel.domain.repository.azkar.AzkarRepository
import com.abueltaweel.domain.repository.bugReport.BugReportRepository
import com.abueltaweel.domain.repository.location.LocationRepository
import com.abueltaweel.domain.repository.network.NetworkConnectionRepository
import com.abueltaweel.domain.repository.prayer.PrayerAlarmRepository
import com.abueltaweel.domain.repository.prayer.PrayerNotificationsRepository
import com.abueltaweel.domain.repository.prayer.PrayerRepository
import com.abueltaweel.domain.repository.qiblah.QiblahRepository
import com.abueltaweel.domain.repository.quran.BookmarkRepository
import com.abueltaweel.domain.repository.quran.QuranRepository
import com.abueltaweel.domain.repository.quran.ReadingProgressRepository
import com.abueltaweel.domain.repository.radio.RadioRepository
import com.abueltaweel.domain.repository.settings.BatteryOptimizationRepository
import com.abueltaweel.domain.repository.settings.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<PrayerRepository> { PrayerRepositoryImpl() }
    single<QiblahRepository> { QiblahRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<PrayerNotificationsRepository> { PrayerNotificationsRepositoryImpl(get()) }
    single<NetworkConnectionRepository> { NetworkConnectionRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single<AzkarRepository> { AzkarRepositoryImpl(get()) }
    single<BatteryOptimizationRepository> { BatteryOptimizationRepositoryImpl(get(), get()) }
    single<QuranRepository> { QuranRepositoryImpl(get(), get(),get()) }
    single<ReadingProgressRepository> { ReadingProgressRepositoryImpl(get()) }
    single<RadioRepository> { RadioRepositoryImpl(get()) }
    single<BookmarkRepository> {
        BookmarkRepositoryImpl(
            dao = get()
        )
    }
    single<BugReportRepository> {
        BugReportRepositoryImpl(
            get()
        )
    }
    single<PrayerAlarmRepository> {
        PrayerAlarmRepositoryImpl(
            context = androidContext(),
            alarmScheduler = get()
        )
    }
}