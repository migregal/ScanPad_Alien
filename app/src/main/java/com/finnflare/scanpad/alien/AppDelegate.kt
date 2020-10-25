package com.finnflare.scanpad.alien

import androidx.multidex.MultiDexApplication
import com.finnflare.scanpad.alien.di.presentersModule
import com.finnflare.scanpad.alien.di.repositoryModule
import com.finnflare.scanpad.alien.di.viewModelsModule
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppDelegate : MultiDexApplication() {
    @ObsoleteCoroutinesApi
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AppDelegate)
            koin.loadModules(
                listOf(
                    repositoryModule,
                    viewModelsModule,
                    presentersModule
                )
            )
            koin.createRootScope()
        }
    }
}