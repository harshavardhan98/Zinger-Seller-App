package com.food.ordering.zinger.seller.data.local

import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.data.model.UserModel

interface AppPreferencesHelper {

    val name: String?
    val email: String?
    val mobile: String?
    val role: String?
    val oauthId: String?
    val shop: String?
    val id: Int?

    fun saveUser(id: Int?,name: String?,email: String?, mobile: String?, role: String?, oauthId: String?, shop: String?)

    fun clearPreferences()

    fun getShop():List<ShopConfigurationModel>?

    fun getUser(): UserModel?
}