package com.food.ordering.zinger.seller.data.retrofit

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.ui.login.LoginActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.HTTP

class AuthInterceptor(val context: Context, val preferences: PreferencesHelper) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val whiteListedEndpoints = listOf(
            "/user/seller",
            "/user/accept/invite"
        )

        val request =
            if (req.url().encodedPath().contains("/user/verify/invite/")) {
                req.newBuilder().build()
            } else if (!whiteListedEndpoints.contains(req.url().encodedPath())) {
                req.newBuilder()
                    .addHeader("oauth_id", preferences.oauthId+"1")
                    .addHeader("id", preferences.id.toString())
                    .addHeader("role", preferences.role)
                    .build()
            } else {
                req.newBuilder().build()
            }
        val response = chain.proceed(request)

        if(response.code() == 412){
            // 412 means the user is unauthorised to use the app
            FirebaseAuth.getInstance().signOut()
            preferences.getShop()?.forEach {
                FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(
                        AppConstants.NOTIFICATION_TOPIC_SHOP_ZINGER + it.shopModel.id
                    );
            }
            FirebaseMessaging.getInstance()
                .unsubscribeFromTopic(AppConstants.NOTIFICATION_TOPIC_GLOBAL);
            preferences.clearPreferences()
            val intent = Intent(context,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        return response
    }
}