package com.food.ordering.zinger.seller.data.retofit

import android.content.Context
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val context: Context, val preferences: PreferencesHelper) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val whiteListedEndpoints = listOf(
            "/user/seller"
        )
        /*//Check if device is connected to internet
        if (!NetworkUtils.isOnline(context)) {
            throw NoConnectivityException()
        }*/

        val request = if (!whiteListedEndpoints.contains(req.url().encodedPath())) {
            println("oauth_id testing 1"+preferences.oauthId)
            // TODO replace with the name in shared preference
            req.newBuilder()
                .addHeader("oauth_id", "auth_9176786581")
                .addHeader("id", "3")
                .addHeader("role", "SHOP_OWNER")
                .build()
        } else {
            println("oauth_id testing 2"+preferences.oauthId)
            req.newBuilder().build()
        }
        val response = chain.proceed(request)
        //Check for UnAuthenticated Request
        /*if (response.code() == HTTP_UNAUTHORIZED) {
            if(whiteListedEndpoints.contains(req.url().encodedPath())){
                throw InvalidCredentialsException()
            }else {
                (context as NiaClubApp).onCustomAppAuthFailed()
                throw CustomAppUnAuthorizedException()
            }
        }*/
        return response
    }
}