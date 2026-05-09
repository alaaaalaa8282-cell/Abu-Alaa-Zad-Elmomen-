package com.abueltaweel.presentation.base

import android.app.Application
import com.abueltaweel.data.di.dataModule
import com.abueltaweel.domain.di.domainModule
import com.abueltaweel.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.maplibre.android.MapLibre

class MehrabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MehrabApplication)
            modules(presentationModule, domainModule, *dataModule.toTypedArray())
        }
        MapLibre.getInstance(this)
    }
}
