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
    var configurationModel: ConfigurationModel,
    @SerializedName("ratingModel")
    var ratingModel: RatingModel,
    @SerializedName("shopModel")
    var shopModel: ShopModel,
    var isSelected: Boolean = false
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
    val role: String? = null,
    @SerializedName("notificationToken")
    val notificationToken: String? = null
)


data class ConfigurationModel(
    @SerializedName("deliveryPrice")
    var deliveryPrice: Double,
    @SerializedName("isDeliveryAvailable")
    var isDeliveryAvailable: Int,
    @SerializedName("isOrderTaken")
    var isOrderTaken: Int,
    @SerializedName("merchantId")
    val merchantId: String? =" ",
    @SerializedName("shopModel")
    var shopModel: ShopModel?
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
    var closingTime: String? = null,
    @SerializedName("coverUrls")
    var coverUrls: ArrayList<String>? =null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("mobile")
    val mobile: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("openingTime")
    var openingTime: String? = null,
    @SerializedName("photoUrl")
    var photoUrl: String? = null,
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