package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName


data class OrderResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Transaction,
    @SerializedName("message")
    val message: String
)

data class Transaction(
    @SerializedName("bankName")
    val bankName: String,
    @SerializedName("bankTransactionId")
    val bankTransactionId: String,
    @SerializedName("checksumHash")
    val checksumHash: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("gatewayName")
    val gatewayName: String,
    @SerializedName("orderModel")
    val orderModel: OrderModel,
    @SerializedName("paymentMode")
    val paymentMode: String,
    @SerializedName("responseCode")
    val responseCode: String,
    @SerializedName("responseMessage")
    val responseMessage: String,
    @SerializedName("transactionId")
    val transactionId: String
)

data class OrderModel(
    @SerializedName("cookingInfo")
    val cookingInfo: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("deliveryLocation")
    val deliveryLocation: String?,
    @SerializedName("deliveryPrice")
    val deliveryPrice: Double,
    @SerializedName("id")
    val id: String,
    @SerializedName("lastStatusUpdatedTime")
    val lastStatusUpdatedTime: String,
    @SerializedName("orderStatus")
    val orderStatus: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("secretKey")
    val secretKey: String?,
    @SerializedName("shopModel")
    val shopModel: ShopModel,
    @SerializedName("userModel")
    val userModel: UserModel
)

