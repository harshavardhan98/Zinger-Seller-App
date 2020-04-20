package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.UserModel
import retrofit2.Retrofit

class UserRespository(private val retofit: Retrofit) {
    val retrofitVar = retofit.create(CustomApi::class.java)
    suspend fun login(userModel: UserModel) = retrofitVar.login(userModel)
}