package com.mafazaa.ainaa

import android.content.Context.*
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.model.repo.*
import org.koin.android.ext.koin.*
import org.koin.core.module.dsl.*
import org.koin.dsl.*

val appModule = module {

    single<RemoteRepo> { if (BuildConfig.DEBUG) FakeRemoteRepo else KtorRepo() }
    single<LocalData> { LocalData(androidContext().getSharedPreferences("App", MODE_PRIVATE)) }
    single<FileRepo> { RealFileRepo(androidContext()) }
    // single<UpdateRepo> { UpdateManager(get(), get(), get()) }

    viewModel { MainViewModel(get(), get(), get() /* , get() */) }
}
