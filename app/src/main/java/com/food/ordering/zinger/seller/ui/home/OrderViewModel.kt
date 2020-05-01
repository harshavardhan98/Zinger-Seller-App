package com.food.ordering.zinger.seller.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.*
import com.food.ordering.zinger.seller.data.retrofit.OrderRepository
import com.food.ordering.zinger.seller.data.retrofit.ShopRepository
import com.food.ordering.zinger.seller.data.retrofit.UserRespository
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import kotlin.Exception

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val userRespository: UserRespository,
    private val shopRepository: ShopRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {


    private val orderByIdRequest = MutableLiveData<Resource<Response<OrderItemListModel>>>()
    val orderByIdResponse: LiveData<Resource<Response<OrderItemListModel>>>
        get() = orderByIdRequest

    fun getOrderById(orderId: Int) {
        viewModelScope.launch {
            try {
                orderByIdRequest.value = Resource.loading()
                val response = orderRepository.getOrderById(orderId)
                if (response.code == 1)
                    orderByIdRequest.value = Resource.success(response)
                else {
                    orderByIdRequest.value = Resource.error(message = response.message)
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    orderByIdRequest.value = Resource.offlineError()
                } else {
                    orderByIdRequest.value = Resource.error(e)
                }
            }
        }
    }

    /*****************************************************************************/

    private val orderByShopId = MutableLiveData<Resource<List<OrderItemListModel>>>()
    val orderByShopIdResponse: LiveData<Resource<List<OrderItemListModel>>>
        get() = orderByShopId

    fun getOrderByShopId(shopId: Int) {
        viewModelScope.launch {
            try {
                orderByShopId.value = Resource.loading()
                val response = orderRepository.getOrderByShopId(shopId)

                if (!response.data.isNullOrEmpty()) {
                    var orders = response.data
                    orderByShopId.value = Resource.success(orders)
                    preferencesHelper.orderStatusChanged = false
                } else {
                    orderByShopId.value = Resource.empty()
                }

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    orderByShopId.value = Resource.offlineError()
                } else {
                    orderByShopId.value = Resource.error(e)
                }
            }
        }
    }

    /*****************************************************************************/

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

    /*****************************************************************************/

    private val getShopDetail = MutableLiveData<Resource<Response<ShopConfigurationModel>>>()
    val getShopDetailResponse: LiveData<Resource<Response<ShopConfigurationModel>>>
        get() = getShopDetail

    fun getShopDetail(id: Int) {
        viewModelScope.launch {
            try {
                getShopDetail.value = Resource.loading()
                val response = shopRepository.getShopDetailsById(id)
                if (response.code == 1)
                    getShopDetail.value = Resource.success(response)
                else
                    getShopDetail.value = Resource.error(message = response.message)
            } catch (e: Exception) {
                println("fetch stats failed ${e.message}")
                if (e is UnknownHostException) {
                    getShopDetail.value = Resource.offlineError()
                } else {
                    getShopDetail.value = Resource.error(e)
                }
            }
        }
    }

}