package com.food.ordering.zinger.seller.ui.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.data.model.UserShopListModel
import com.food.ordering.zinger.seller.data.retrofit.UserRespository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class OTPViewModel(private val userRepository: UserRespository) : ViewModel() {

    private val performLogin = MutableLiveData<Resource<Response<UserShopListModel>>>()
    val performLoginStatus: LiveData<Resource<Response<UserShopListModel>>>
        get() = performLogin

    fun login(userModel: UserModel) {
        viewModelScope.launch {
            try {
                performLogin.value = Resource.loading()
                val response = userRepository.login(userModel)
                if(response.code==1)
                    performLogin.value = Resource.success(response)
                else
                    performLogin.value = Resource.error(message = response.message)
            } catch (e: Exception) {
                println("fetch stats failed ${e.message}")
                if (e is UnknownHostException) {
                    performLogin.value = Resource.offlineError()
                } else {
                    performLogin.value = Resource.error(e)
                }
            }
        }
    }
}