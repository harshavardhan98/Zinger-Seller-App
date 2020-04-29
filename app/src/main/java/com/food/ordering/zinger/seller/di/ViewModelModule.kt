package com.food.ordering.zinger.seller.di

import com.food.ordering.zinger.seller.ui.home.HomeViewModel
import com.food.ordering.zinger.seller.ui.profile.ProfileViewModel
import com.food.ordering.zinger.seller.ui.login.LoginViewModel
import com.food.ordering.zinger.seller.ui.menu.MenuViewModel
import com.food.ordering.zinger.seller.ui.menuItem.MenuItemViewModel
import com.food.ordering.zinger.seller.ui.order.OrderViewModel
import com.food.ordering.zinger.seller.ui.orderDetail.OrderDetailViewModel
import com.food.ordering.zinger.seller.ui.otp.OTPViewModel
import com.food.ordering.zinger.seller.ui.seller.SellerViewModel
import com.food.ordering.zinger.seller.ui.shopProfile.ShopProfileViewModel
import com.food.ordering.zinger.seller.ui.verifyInvite.InviteSellerViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get())}
    viewModel { MenuItemViewModel(get()) }
    viewModel { MenuViewModel(get()) }
    viewModel { OrderViewModel(get(),get(),get()) }
    viewModel { SellerViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { OTPViewModel(get(),get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ShopProfileViewModel(get()) }
    viewModel { OrderDetailViewModel(get()) }
    viewModel { InviteSellerViewModel(get()) }
}
