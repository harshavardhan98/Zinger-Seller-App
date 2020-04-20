package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.UserShopModel
import retrofit2.Retrofit

class SellerRepository(private val retrofit: Retrofit) {

    val retrofiVar = retrofit.create(CustomApi::class.java)

    suspend fun getSeller(shopId: String) = retrofiVar.getSellers(shopId)

    suspend fun inviteSeller(userShopModel: UserShopModel) = retrofiVar.inviteSeller(userShopModel)

    suspend fun verifyInvite(shopId: Int, mobile: String) = retrofiVar.verifyInvite(shopId, mobile)

    suspend fun acceptInvite(userShopModel: UserShopModel) = retrofiVar.acceptInvite(userShopModel)

    suspend fun deleteInvite(userShopModel: UserShopModel) = retrofiVar.deleteInvite(userShopModel)

    suspend fun notifyInvite(userShopModel: UserShopModel) = retrofiVar.notifyInvite(userShopModel)

}