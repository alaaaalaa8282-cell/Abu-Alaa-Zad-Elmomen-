package com.abueltaweel.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.abueltaweel.data.azkar.local.AzkarLocalDataSource
import com.abueltaweel.data.util.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
private const val DATASTORE_NAME = "location_prefs"

val Context.locationDataStore by preferencesDataStore(name = DATASTORE_NAME)
val localModule = module {
    single { get<Context>().locationDataStore }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "mehrab_database"
        ).build()
    }
    single { get<AppDatabase>().bookmarkDao() }
    single<AzkarLocalDataSource> { AzkarLocalDataSource(get(), get()) }
}