package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("isDelete")
    val isDelete: Int,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("oauthId")
    val oauthId: String,
    @SerializedName("role")
    val role: String
)