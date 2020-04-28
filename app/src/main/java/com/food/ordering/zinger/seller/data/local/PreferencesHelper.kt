package com.food.ordering.zinger.seller.data.local

import android.content.Context
import android.content.SharedPreferences
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesHelper(context: Context) : AppPreferencesHelper {

    private val loginPreferences: SharedPreferences =
        context.getSharedPreferences(AppConstants.PREFS_LOGIN_PREFS, Context.MODE_PRIVATE)
    private val sellerPreferences: SharedPreferences = context.getSharedPreferences(
        AppConstants.PREFS_CUSTOMER,
        Context.MODE_PRIVATE
    )

    override var id: Int?
        get() = loginPreferences.getInt(AppConstants.PREFS_SELLER_ID,-1)
        set(value) {
            if(value!=null)
                loginPreferences.edit().putInt(AppConstants.PREFS_SELLER_ID,value)
        }

    override var name: String?
        get() = sellerPreferences.getString(AppConstants.PREFS_SELLER_NAME, null)
        set(value) = sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_NAME, value).apply()

    override var email: String?
        get() = sellerPreferences.getString(AppConstants.PREFS_SELLER_EMAIL, null)
        set(value) = sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_EMAIL, value).apply()

    override var mobile: String?
        get() = sellerPreferences.getString(AppConstants.PREFS_SELLER_MOBILE, null)
        set(value) = sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_MOBILE, value).apply()

    override var role: String?
        get() = sellerPreferences.getString(AppConstants.PREFS_SELLER_ROLE, null)
        set(value) = sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_ROLE, value).apply()

    override var oauthId: String?
        get() = loginPreferences.getString(AppConstants.PREFS_AUTH_TOKEN, null)
        set(value) = loginPreferences.edit().putString(AppConstants.PREFS_AUTH_TOKEN, value).apply()

    override var shop: String?
        get() = sellerPreferences.getString(AppConstants.PREFS_SELLER_PLACE, null)
        set(value) = sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_PLACE, value).apply()

    override var currentShop: Int
        get() = sellerPreferences.getInt(AppConstants.PREFS_CURRENT_SHOP_ID, 0)
        set(value) = sellerPreferences.edit().putInt(AppConstants.PREFS_CURRENT_SHOP_ID, value).apply()

    override var fcmToken: String?
        get() = sellerPreferences.getString(AppConstants.PREFS_SELLER_FCM_TOKEN,"test")
        set(value) = sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_FCM_TOKEN, value).apply()

    override var isFCMTokenUpdated: Boolean?
        get() = loginPreferences.getBoolean(AppConstants.PREFS_IS_FCM_TOKEN_GENERATED,false)
        set(value) {
            if(value!=null)
                loginPreferences.edit().putBoolean(AppConstants.PREFS_IS_FCM_TOKEN_GENERATED,value).apply()
        }

    override var isFCMTopicSubScribed: Boolean?
        get() = loginPreferences.getBoolean(AppConstants.PREFS_IS_FCM_TOPIC_SUBSCRIBED,false)
        set(value) {
            if(value!=null)
                loginPreferences.edit().putBoolean(AppConstants.PREFS_IS_FCM_TOPIC_SUBSCRIBED,value).apply()
        }

    override fun saveUser(id: Int?, name: String?, email: String?, mobile: String?, role: String?, oauthId: String?, shop: String?) {
        sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_NAME, name).apply()
        sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_EMAIL, email).apply()
        //sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_MOBILE, mobile).apply()
        sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_ROLE, role).apply()
        if (id != null) {
            loginPreferences.edit().putInt(AppConstants.PREFS_SELLER_ID,id).apply()
        }
        //loginPreferences.edit().putString(AppConstants.PREFS_AUTH_TOKEN, oauthId).apply()
        sellerPreferences.edit().putString(AppConstants.PREFS_SELLER_PLACE, shop).apply()
    }

    override fun getShop(): List<ShopConfigurationModel>? {
        val listType = object : TypeToken<List<ShopConfigurationModel?>?>() {}.type
        return Gson().fromJson(shop, listType)
    }

    override fun getUser(): UserModel? {
        return UserModel(id,email, mobile, name, oauthId, role)
    }

    override fun clearPreferences() {
        loginPreferences.edit().clear().apply()
        sellerPreferences.edit().clear().apply()
    }
}
