package com.food.ordering.zinger.seller.data.retrofit

import com.food.ordering.zinger.seller.data.model.ConfigurationModel
import retrofit2.Retrofit

class ShopRepository(private val retrofit: Retrofit) {

    val retrofitVar =retrofit.create(CustomApi::class.java)

    suspend fun updateShopConfiguration(shopConfigRequest: ConfigurationModel) = retrofitVar.updateShopConfiguration(shopConfigRequest)

}