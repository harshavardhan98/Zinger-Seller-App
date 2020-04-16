package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName


data class ShopConfigRequest(
    @SerializedName("deliveryPrice")
    val deliveryPrice: Int,
    @SerializedName("isDeliveryAvailable")
    val isDeliveryAvailable: Int,
    @SerializedName("isOrderTaken")
    val isOrderTaken: Int,
    @SerializedName("merchantId")
    val merchantId: String,
    @SerializedName("shopModel")
    val shopModel: ShopModel
)

