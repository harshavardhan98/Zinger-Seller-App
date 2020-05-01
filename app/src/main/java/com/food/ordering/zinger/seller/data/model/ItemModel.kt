package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName



data class ItemModel(
    @SerializedName("category")
    var category: String,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("isAvailable")
    var isAvailable: Int?,
    @SerializedName("isVeg")
    var isVeg: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("photoUrl")
    var photoUrl: String,
    @SerializedName("price")
    var price: Double,
    @SerializedName("shopModel")
    var shopModel: ShopModel?
)