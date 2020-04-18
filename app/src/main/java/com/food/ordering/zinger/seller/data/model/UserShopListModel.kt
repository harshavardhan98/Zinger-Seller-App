package com.food.ordering.zinger.seller.data.model
import com.google.gson.annotations.SerializedName



data class UserShopListModel(
    @SerializedName("shopModelList")
    val shopModelList: List<ShopConfigurationModel>,
    @SerializedName("userModel")
    val userModel: UserModel
)

data class ShopConfigurationModel(
    @SerializedName("configurationModel")
    val configurationModel: ConfigurationModel,
    @SerializedName("ratingModel")
    val ratingModel: RatingModel,
    @SerializedName("shopModel")
    val shopModel: ShopModel
)


data class UserModel(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("mobile")
    val mobile: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("oauthId")
    val oauthId: String? = null,
    @SerializedName("role")
    val role: String? = null
)


data class ConfigurationModel(
    @SerializedName("deliveryPrice")
    val deliveryPrice: Double,
    @SerializedName("isDeliveryAvailable")
    val isDeliveryAvailable: Int,
    @SerializedName("isOrderTaken")
    val isOrderTaken: Int,
    @SerializedName("merchantId")
    val merchantId: String,
    @SerializedName("shopModel")
    val shopModel: ShopModel?
)

data class RatingModel(
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("shopModel")
    val shopModel: ShopModel,
    @SerializedName("userCount")
    val userCount: Int
)

data class ShopModel(
    @SerializedName("closingTime")
    val closingTime: String? = null,
    @SerializedName("coverUrls")
    val coverUrls: List<String>? = null ,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("mobile")
    val mobile: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("openingTime")
    val openingTime: String? = null,
    @SerializedName("photoUrl")
    val photoUrl: String? = null,
    @SerializedName("placeModel")
    val placeModel: PlaceModel? = null
)

data class PlaceModel(
    @SerializedName("address")
    val address: String,
    @SerializedName("iconUrl")
    val iconUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)