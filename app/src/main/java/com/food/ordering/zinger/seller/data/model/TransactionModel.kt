package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName



data class TransactionModel(
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
    val cookingInfo: String?=null,
    @SerializedName("date")
    val date: String?=null,
    @SerializedName("deliveryLocation")
    val deliveryLocation: String?=null,
    @SerializedName("deliveryPrice")
    val deliveryPrice: Double?=null,
    @SerializedName("id")
    val id: String?=null,
    @SerializedName("lastStatusUpdatedTime")
    val lastStatusUpdatedTime: String?=null,
    @SerializedName("orderStatus")
    val orderStatus: String?=null,
    @SerializedName("price")
    val price: Double?=null,
    @SerializedName("rating")
    val rating: Double?=null,
    @SerializedName("secretKey")
    val secretKey: String?=null,
    @SerializedName("shopModel")
    val shopModel: ShopModel?=null,
    @SerializedName("userModel")
    val userModel: UserModel?=null
)

