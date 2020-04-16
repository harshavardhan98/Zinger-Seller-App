package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName


data class MenuResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<Item>,
    @SerializedName("message")
    val message: String
)

data class Item(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("isAvailable")
    val isAvailable: Int?,
    @SerializedName("isVeg")
    val isVeg: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("shopModel")
    val shopModel: ShopModel?
)