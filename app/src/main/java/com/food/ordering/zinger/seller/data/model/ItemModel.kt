package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName



data class ItemModel(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("isAvailable")
    var isAvailable: Int?,
    @SerializedName("isVeg")
    var isVeg: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("shopModel")
    val shopModel: ShopModel?
)