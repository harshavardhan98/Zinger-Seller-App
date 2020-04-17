package com.food.ordering.zinger.seller.data.local

import android.content.Context
import android.content.SharedPreferences
import com.food.ordering.zinger.seller.data.model.PlaceModel
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesHelper(context: Context) : AppPreferencesHelper {

    private val loginPreferences: SharedPreferences =
        context.getSharedPreferences(AppConstants.PREFS_LOGIN_PREFS, Context.MODE_PRIVATE)
    private val customerPreferences: SharedPreferences = context.getSharedPreferences(
        AppConstants.PREFS_CUSTOMER,
        Context.MODE_PRIVATE
    )

    override var name: String?
        get() = customerPreferences.getString(AppConstants.PREFS_SELLER_NAME, null)
        set(value) = customerPreferences.edit().putString(AppConstants.PREFS_SELLER_NAME, value).apply()

    override var email: String?
        get() = customerPreferences.getString(AppConstants.PREFS_SELLER_EMAIL, null)
        set(value) = customerPreferences.edit().putString(AppConstants.PREFS_SELLER_EMAIL, value).apply()

    override var mobile: String?
        get() = customerPreferences.getString(AppConstants.PREFS_SELLER_MOBILE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.PREFS_SELLER_MOBILE, value).apply()

    override var role: String?
        get() = customerPreferences.getString(AppConstants.PREFS_SELLER_ROLE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.PREFS_SELLER_ROLE, value).apply()

    override var oauthId: String?
        get() = loginPreferences.getString(AppConstants.PREFS_AUTH_TOKEN, null)
        set(value) = loginPreferences.edit().putString(AppConstants.PREFS_AUTH_TOKEN, value).apply()

    override var shop: String?
        get() = customerPreferences.getString(AppConstants.PREFS_SELLER_PLACE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.PREFS_SELLER_PLACE, value).apply()

    override fun saveUser(name: String?, email: String?, mobile: String?, role: String?, oauthId: String?, shop: String?) {
        customerPreferences.edit().putString(AppConstants.PREFS_SELLER_NAME, name).apply()
        customerPreferences.edit().putString(AppConstants.PREFS_SELLER_EMAIL, email).apply()
        customerPreferences.edit().putString(AppConstants.PREFS_SELLER_MOBILE, mobile).apply()
        customerPreferences.edit().putString(AppConstants.PREFS_SELLER_ROLE, role).apply()
        loginPreferences.edit().putString(AppConstants.PREFS_AUTH_TOKEN, oauthId).apply()
        customerPreferences.edit().putString(AppConstants.PREFS_SELLER_PLACE, shop).apply()
    }

    fun getShop(): List<ShopConfigurationModel>? {
        val listType = object : TypeToken<List<ShopConfigurationModel?>?>() {}.type
        return Gson().fromJson(shop, listType)
    }

    fun getUser(): UserModel? {
        return UserModel(email, mobile, name, oauthId, role)
    }

}
