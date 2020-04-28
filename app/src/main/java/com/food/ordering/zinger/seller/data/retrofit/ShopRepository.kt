package com.food.ordering.zinger.seller.data.retrofit

import com.food.ordering.zinger.seller.data.model.ConfigurationModel
import retrofit2.Retrofit

class ShopRepository(private val retrofit: Retrofit) {

    val service =retrofit.create(CustomApi::class.java)

    suspend fun updateShopConfiguration(shopConfigRequest: ConfigurationModel) = service.updateShopConfiguration(shopConfigRequest)

    suspend fun getShopDetailsById(shopId: Int) = service.getShopDetailsById(shopId = shopId)

}