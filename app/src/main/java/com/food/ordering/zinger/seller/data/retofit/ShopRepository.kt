package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.ShopConfigRequest
import retrofit2.Retrofit

class ShopRepository(private val retrofit: Retrofit) {

    // suspend fun getShopMenu(shopId: Int) = retrofit.create(CustomApi::class.java).getShopMenu(shopId);
    suspend fun updateShopConfiguration(shopConfigRequest: ShopConfigRequest) =
        retrofit.create(CustomApi::class.java).updateShopConfiguration(shopConfigRequest)

}