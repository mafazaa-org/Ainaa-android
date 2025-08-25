package com.mafazaa.ainaa.domain.di

import android.content.Context.MODE_PRIVATE
import com.mafazaa.ainaa.data.repository_impl.KtorRepo
import com.mafazaa.ainaa.data.repository_impl.data_source.local_data_source.LocalData
import com.mafazaa.ainaa.data.repository_impl.data_source.remote_data_source.RemoteRepo
import com.mafazaa.ainaa.data.repository_impl.view_models.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.*
import org.koin.dsl.*

val appModule = module {

    single<RemoteRepo> { KtorRepo() }
    single<LocalData> { LocalData(androidContext().getSharedPreferences("App", MODE_PRIVATE)) }

    viewModel { MainViewModel(get(), get()) }
}
