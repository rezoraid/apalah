package com.komikta.app

import android.app.Application
import com.komikta.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class KomiktaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
            androidContext(this@KomiktaApp)
            modules(appModule)
        }
    }
}
