package com.abueltaweel.domain.di

import com.abueltaweel.domain.usecase.PrayerSchedulingUseCase
import org.koin.dsl.module

val domainModule = module {
    single { PrayerSchedulingUseCase(get(), get(), get(), get()) }
}