package com.food.ordering.zinger.seller.data.model

data class OrderStatus(
    var isDone: Boolean = false,
    var isCurrent: Boolean = false,
    var name: String,
    var orderStatusList: List<OrderStatusItemModel> = listOf()
)
