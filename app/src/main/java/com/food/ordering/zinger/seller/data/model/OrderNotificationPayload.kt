package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName


data class OrderNotificationPayload(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("itemList")
    val itemList: List<String>,
    @SerializedName("orderId")
    val orderId: Int,
    @SerializedName("userName")
    val userName: String
)