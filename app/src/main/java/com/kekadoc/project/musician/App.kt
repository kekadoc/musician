package com.kekadoc.project.musician

import android.app.Application
import com.kekadoc.project.musician.module.exoPlayerModule
import com.kekadoc.project.musician.module.fakeModels
import com.kekadoc.project.musician.module.networkModule
import com.kekadoc.project.musician.module.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(networkModule, viewModelsModule, exoPlayerModule) //, fakeModels
        }
    }
}