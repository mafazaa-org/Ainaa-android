package com.mafazaa.ainaa

import android.content.Context.MODE_PRIVATE
import com.mafazaa.ainaa.data.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.*
import org.koin.dsl.*

val appModule = module {

    single<RemoteRepo> {  KtorRepo() }
    single<LocalData> { LocalData(androidContext().getSharedPreferences("App",MODE_PRIVATE)) }

    viewModel { MainViewModel(get(),get()) }
}
