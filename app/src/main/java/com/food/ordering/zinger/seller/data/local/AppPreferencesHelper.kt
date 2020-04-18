package com.food.ordering.zinger.seller.data.local

interface AppPreferencesHelper {

    val name: String?
    val email: String?
    val mobile: String?
    val role: String?
    val oauthId: String?
    val shop: String?
    val id: Int?

    fun saveUser(id: Int?,name: String?,email: String?, mobile: String?, role: String?, oauthId: String?, shop: String?)

}