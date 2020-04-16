package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName

data class NotifyRequest(
    @SerializedName("shopModel")
    val shopModel: ShopModel,
    @SerializedName("userModel")
    val userModel: UserModel
)
