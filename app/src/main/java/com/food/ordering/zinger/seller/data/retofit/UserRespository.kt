package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.UserModel
import retrofit2.Retrofit

class UserRespository(private val retofit: Retrofit) {

    suspend fun login(userModel: UserModel) = retofit.create(CustomApi::class.java).login(userModel)
}