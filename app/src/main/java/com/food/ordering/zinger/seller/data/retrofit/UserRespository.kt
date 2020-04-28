package com.food.ordering.zinger.seller.data.retrofit

import com.food.ordering.zinger.seller.data.model.UserModel
import retrofit2.Retrofit

class UserRespository(private val retofit: Retrofit) {
    val service = retofit.create(CustomApi::class.java)

    suspend fun login(userModel: UserModel) = service.login(userModel)

    suspend fun updateProfile(userModel: UserModel) = service.updateProfile(userModel)
}