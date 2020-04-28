package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName



data class OrderItemListModel(
    @SerializedName("orderItemsList")
    val orderItemsList: List<OrderItems>,
    @SerializedName("transactionModel")
    val transactionModel: TransactionModel,
    @SerializedName("orderStatusModel")
    val orderStatusModel: ArrayList<OrderStatusItemModel>
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

data class OrderStatusItemModel(
    @SerializedName("orderId")
    val orderId: String?,
    @SerializedName("orderStatus")
    val orderStatus: String,
    @SerializedName("updatedTime")
    val updatedTime: String
)

