package com.armdroid.playground

import android.app.Application
import com.armdroid.playground.common.CommonContract
import com.armdroid.playground.common.CommonPresenter
import com.armdroid.playground.home.HomeContract
import com.armdroid.playground.home.HomePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(modules)
        }
    }

    val modules
        get() = listOf(module {
            single { Repository() }

            factory { HomePresenter(get()) } bind HomeContract.Presenter::class

            factory(named("common1")) { CommonPresenter(get()) } bind CommonContract.Presenter::class

            factory(named("common2")) { CommonPresenter(get()) } bind CommonContract.Presenter::class
        })
}