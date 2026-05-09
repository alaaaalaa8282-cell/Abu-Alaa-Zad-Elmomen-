package com.abueltaweel.data.di

import com.google.gson.Gson
import com.abueltaweel.presentation.utils.AlarmScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { Gson() }
    single { AlarmScheduler(androidContext()) }
}