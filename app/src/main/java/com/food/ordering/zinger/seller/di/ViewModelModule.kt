package com.food.ordering.zinger.seller.di

import com.food.ordering.zinger.seller.ui.menu.MenuViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MenuViewModel(get())}
}
