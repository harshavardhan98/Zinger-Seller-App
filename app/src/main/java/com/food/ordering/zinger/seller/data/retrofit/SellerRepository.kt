package com.food.ordering.zinger.seller.data.retrofit

import com.food.ordering.zinger.seller.data.model.UserShopModel
import retrofit2.Retrofit

class SellerRepository(private val retrofit: Retrofit) {

    val service = retrofit.create(CustomApi::class.java)

    suspend fun getSeller(shopId: String) = service.getSellers(shopId)

    suspend fun inviteSeller(userShopModel: UserShopModel) = service.inviteSeller(userShopModel)

    suspend fun verifyInvite(shopId: Int, mobile: String) = service.verifyInvite(shopId, mobile)

    suspend fun acceptInvite(userShopModel: UserShopModel) = service.acceptInvite(userShopModel)

    suspend fun deleteInvite(userShopModel: UserShopModel) = service.deleteInvite(userShopModel)

    suspend fun notifyInvite(userShopModel: UserShopModel) = service.notifyInvite(userShopModel)

    suspend fun deleteSeller(shopId: Int,userId: Int) = service.deleteSeller(shopId,userId)

}