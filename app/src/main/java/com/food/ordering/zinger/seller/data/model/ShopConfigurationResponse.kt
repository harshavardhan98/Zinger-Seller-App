package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName


data class ShopConfigurationResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: String,
    @SerializedName("message")
    val message: String
)