package com.food.ordering.zinger.seller.di

import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.google.gson.Gson
import org.koin.dsl.module

val appModule = module {

    single {
        Gson()
    }

    single {
        PreferencesHelper(get())
    }

}