package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName


data class SellerListResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<SellersListResponseData>,
    @SerializedName("message")
    val message: String
)

data class SellersListResponseData(
    @SerializedName("email")
    val email: String?,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("oauthId")
    val oauthId: String,
    @SerializedName("role")
    val role: String
)