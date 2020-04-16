package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName



data class OrderItemList(
    @SerializedName("orderItemsList")
    val orderItemsList: List<OrderItems>,
    @SerializedName("transactionModel")
    val transactionModel: TransactionModel
)

data class OrderItems(
    @SerializedName("itemModel")
    val itemModel: ItemModel,
    @SerializedName("orderModel")
    val orderModel: OrderModel?,
    @SerializedName("price")
    val price: Double,
    @SerializedName("quantity")
    val quantity: Int
)

