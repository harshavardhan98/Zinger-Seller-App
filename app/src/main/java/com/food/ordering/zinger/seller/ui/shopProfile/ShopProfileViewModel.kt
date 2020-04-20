package com.food.ordering.zinger.seller.ui.shopProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.model.ShopConfigRequest
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.data.retrofit.ShopRepository
import com.food.ordering.zinger.seller.data.retrofit.UserRespository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class ShopProfileViewModel(private val shopRepository: ShopRepository) : ViewModel() {
    private val performUpdateShopProfile = MutableLiveData<Resource<Response<String>>>()
    val performUpdateShopProfileStatus: LiveData<Resource<Response<String>>>
        get() = performUpdateShopProfile

    fun updateShopProfile(shopConfigRequest: ShopConfigRequest) {
        viewModelScope.launch {
            try {
                performUpdateShopProfile.value = Resource.loading()
                val response = shopRepository.updateShopConfiguration(shopConfigRequest)
                if (response.code == 1)
                    performUpdateShopProfile.value = Resource.success(response)
                else
                    performUpdateShopProfile.value = Resource.error(message = response.message)
            } catch (e: Exception) {
                println("fetch stats failed ${e.message}")
                if (e is UnknownHostException) {
                    performUpdateShopProfile.value = Resource.offlineError()
                } else {
                    performUpdateShopProfile.value = Resource.error(e)
                }
            }
        }
    }
}