package com.food.ordering.zinger.seller.data.retrofit

import android.content.Context
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

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
                    .addHeader("oauth_id", preferences.oauthId)
                    .addHeader("id", preferences.id.toString())
                    .addHeader("role", preferences.role)
                    .build()
            } else {
                req.newBuilder().build()
            }
        val response = chain.proceed(request)
        return response
    }
}