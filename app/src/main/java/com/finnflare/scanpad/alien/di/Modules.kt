package com.finnflare.scanpad.alien.di

import com.finnflare.scanpad.alien.UIViewModel
import com.finnflare.scanpad.database.CAppDatabase
import com.finnflare.scanpad.database.CDatabasePresenter
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module(override = true) {
    single { CAppDatabase.getInstance(androidContext()) }
}

@ObsoleteCoroutinesApi
val viewModelsModule = module(override = true) {
    single { UIViewModel() }
}

@ObsoleteCoroutinesApi
val presentersModule = module(override = true) {
    single { CDatabasePresenter() }
}