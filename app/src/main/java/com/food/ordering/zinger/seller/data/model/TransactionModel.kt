package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName
import java.util.*


data class TransactionModel(
    @SerializedName("bankName")
    val bankName: String ?= "",
    @SerializedName("bankTransactionId")
    val bankTransactionId: String =" ",
    @SerializedName("checksumHash")
    val checksumHash: String ?=" ",
    @SerializedName("currency")
    val currency: String ?= "",
    @SerializedName("gatewayName")
    val gatewayName: String ?= "",
    @SerializedName("orderModel")
    val orderModel: OrderModel,
    @SerializedName("paymentMode")
    val paymentMode: String ?= "",
    @SerializedName("responseCode")
    val responseCode: String ?= "",
    @SerializedName("responseMessage")
    val responseMessage: String ?= "",
    @SerializedName("transactionId")
    val transactionId: String ?= ""
)

data class OrderModel(
    @SerializedName("cookingInfo")
    val cookingInfo: String?=null,
    @SerializedName("date")
    var date: Date?=null,
    @SerializedName("deliveryLocation")
    val deliveryLocation: String?=null,
    @SerializedName("deliveryPrice")
    val deliveryPrice: Double?=null,
    @SerializedName("id")
    var id: Int?=null,
    @SerializedName("lastStatusUpdatedTime")
    val lastStatusUpdatedTime: Date?=null,
    @SerializedName("orderStatus")
    var orderStatus: String?=null,
    @SerializedName("price")
    val price: Double?=null,
    @SerializedName("rating")
    val rating: Double?=null,
    @SerializedName("feedback")
    val feedBack: String?=null,
    @SerializedName("secretKey")
    var secretKey: String?=null,
    @SerializedName("shopModel")
    val shopModel: ShopModel?=null,
    @SerializedName("userModel")
    val userModel: UserModel?=null
)

