package com.food.ordering.zinger.seller

import android.app.Application
import com.food.ordering.zinger.seller.di.appModule
import com.food.ordering.zinger.seller.di.networkModule
import com.food.ordering.zinger.seller.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ZingerSeller: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ZingerSeller)
            modules(listOf(appModule, networkModule, viewModelModule))
        }
    }
}