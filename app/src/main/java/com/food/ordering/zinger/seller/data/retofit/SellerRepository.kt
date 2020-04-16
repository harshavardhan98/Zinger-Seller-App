package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.UserShopModel
import retrofit2.Retrofit

class SellerRepository(private val retrofit: Retrofit) {

    suspend fun getSeller(shopId: String) = retrofit.create(CustomApi::class.java).getSellers(shopId)

    suspend fun inviteSeller(userShopModel: UserShopModel) =
        retrofit.create(CustomApi::class.java).inviteSeller(userShopModel)

    suspend fun verifyInvite(shopId: Int, mobile: String) =
        retrofit.create(CustomApi::class.java).verifyInvite(shopId, mobile)

    suspend fun acceptInvite(userShopModel: UserShopModel) =
        retrofit.create(CustomApi::class.java).acceptInvite(userShopModel)

    suspend fun deleteInvite(userShopModel: UserShopModel) =
        retrofit.create(CustomApi::class.java).deleteInvite(userShopModel)


    suspend fun notifyInvite(userShopModel: UserShopModel) =
        retrofit.create(CustomApi::class.java).notifyInvite(userShopModel)

}