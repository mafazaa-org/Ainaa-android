package com.mafazaa.ainaa

import android.content.Context.MODE_PRIVATE
import com.mafazaa.ainaa.data.JsEngine
import com.mafazaa.ainaa.data.RealFileRepo
import com.mafazaa.ainaa.data.UpdateManager
import com.mafazaa.ainaa.data.local.LocalData
import com.mafazaa.ainaa.data.remote.FakeRemoteRepo
import com.mafazaa.ainaa.data.remote.KtorRepo
import com.mafazaa.ainaa.model.FileRepo
import com.mafazaa.ainaa.model.repo.RemoteRepo
import com.mafazaa.ainaa.model.repo.ScriptRepo
import com.mafazaa.ainaa.model.repo.UpdateRepo
import com.mafazaa.ainaa.service.OverlayManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<RemoteRepo> { if (BuildConfig.DEBUG) FakeRemoteRepo else KtorRepo() }
    single<LocalData> { LocalData(androidContext().getSharedPreferences("App", MODE_PRIVATE)) }
    single<FileRepo> { RealFileRepo(androidContext()) }
    single<OverlayManager> { OverlayManager(androidContext()) }
    single<ScriptRepo> { JsEngine().apply {  setCodes(
        Constants.defaultCodes
    ) }}
    single<UpdateRepo> { UpdateManager(get(), get(), get()) }

    viewModel { MainViewModel(get(), get(), get(), get()) }

}
