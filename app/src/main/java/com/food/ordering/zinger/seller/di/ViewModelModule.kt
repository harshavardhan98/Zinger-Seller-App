package com.food.ordering.zinger.seller.di

import com.food.ordering.zinger.seller.ui.contributors.ContributorViewModel
import com.food.ordering.zinger.seller.ui.home.HomeViewModel
import com.food.ordering.zinger.seller.ui.profile.ProfileViewModel
import com.food.ordering.zinger.seller.ui.login.LoginViewModel
import com.food.ordering.zinger.seller.ui.menu.MenuViewModel
import com.food.ordering.zinger.seller.ui.menuItem.MenuItemViewModel
import com.food.ordering.zinger.seller.ui.home.OrderViewModel
import com.food.ordering.zinger.seller.ui.orderdetail.OrderDetailViewModel
import com.food.ordering.zinger.seller.ui.orderhistory.OrderHistoryViewModel
import com.food.ordering.zinger.seller.ui.otp.OTPViewModel
import com.food.ordering.zinger.seller.ui.searchorders.SearchOrderViewModel
import com.food.ordering.zinger.seller.ui.shopProfile.ShopProfileViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(),get())}
    viewModel { MenuItemViewModel(get(),get()) }
    viewModel { MenuViewModel(get()) }
    viewModel {
        OrderViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { SearchOrderViewModel(get()) }
    viewModel { OrderHistoryViewModel(get()) }
    viewModel { ProfileViewModel(get(),get()) }
    viewModel { OTPViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ShopProfileViewModel(get()) }
    viewModel { OrderDetailViewModel(get()) }
    viewModel { ContributorViewModel() }
}
