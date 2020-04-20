package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.ShopConfigRequest
import retrofit2.Retrofit

class ShopRepository(private val retrofit: Retrofit) {

    val retrofitVar =retrofit.create(CustomApi::class.java)

    suspend fun updateShopConfiguration(shopConfigRequest: ShopConfigRequest) = retrofitVar.updateShopConfiguration(shopConfigRequest)

}