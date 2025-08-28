package com.mafazaa.ainaa.domain.di

import android.content.Context.MODE_PRIVATE
import com.mafazaa.ainaa.BuildConfig
import com.mafazaa.ainaa.data.RealFileRepo
import com.mafazaa.ainaa.data.UpdateManager
import com.mafazaa.ainaa.data.repository_impl.KtorRepo
import com.mafazaa.ainaa.data.repository_impl.data_source.local_data_source.LocalData
import com.mafazaa.ainaa.data.repository_impl.data_source.remote_data_source.FakeRemoteRepo
import com.mafazaa.ainaa.data.repository_impl.view_models.MainViewModel
import com.mafazaa.ainaa.domain.model.repo.FileRepo
import com.mafazaa.ainaa.domain.model.repo.RemoteRepo
import com.mafazaa.ainaa.domain.model.repo.UpdateRepo
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<RemoteRepo> { if (BuildConfig.DEBUG) FakeRemoteRepo else KtorRepo() }
    single<LocalData> { LocalData(androidContext().getSharedPreferences("App", MODE_PRIVATE)) }
    single<FileRepo> { RealFileRepo(androidContext()) }
    single<UpdateRepo> { UpdateManager(get(), get(), get()) }

    viewModel { MainViewModel(get(), get(), get(), get()) }
}
