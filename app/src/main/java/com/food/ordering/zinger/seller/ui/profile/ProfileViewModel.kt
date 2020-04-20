package com.food.ordering.zinger.seller.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.data.retrofit.UserRespository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class ProfileViewModel(private val userRespository: UserRespository) : ViewModel() {
    private val performUpdateProfile = MutableLiveData<Resource<Response<String>>>()
    val performUpdateProfileStatus: LiveData<Resource<Response<String>>>
        get() = performUpdateProfile

    fun updateProfile(userModel: UserModel) {
        viewModelScope.launch {
            try {
                performUpdateProfile.value = Resource.loading()
                val response = userRespository.updateProfile(userModel)
                if (response.code == 1)
                    performUpdateProfile.value = Resource.success(response)
                else
                    performUpdateProfile.value = Resource.error(message = response.message)
            } catch (e: Exception) {
                println("fetch stats failed ${e.message}")
                if (e is UnknownHostException) {
                    performUpdateProfile.value = Resource.offlineError()
                } else {
                    performUpdateProfile.value = Resource.error(e)
                }
            }
        }
    }
}