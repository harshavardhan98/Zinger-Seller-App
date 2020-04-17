package com.food.ordering.zinger.seller.di

import com.food.ordering.zinger.seller.ui.login.LoginViewModel
import com.food.ordering.zinger.seller.ui.menu.MenuViewModel
import com.food.ordering.zinger.seller.ui.order.OrderViewModel
import com.food.ordering.zinger.seller.ui.otp.OTPViewModel
import com.food.ordering.zinger.seller.ui.seller.SellerViewModel
import com.food.ordering.zinger.seller.ui.shopConfiguration.ShopConfigViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MenuViewModel(get())}
    viewModel { ShopConfigViewModel(get()) }
    viewModel { OrderViewModel(get()) }
    viewModel { SellerViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { OTPViewModel(get()) }
}
