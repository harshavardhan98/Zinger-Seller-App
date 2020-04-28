package com.food.ordering.zinger.seller.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.*
import com.food.ordering.zinger.seller.data.retrofit.OrderRepository
import com.food.ordering.zinger.seller.data.retrofit.ShopRepository
import com.food.ordering.zinger.seller.data.retrofit.UserRespository
import com.food.ordering.zinger.seller.utils.AppConstants
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import kotlin.Exception

class OrderViewModel(private val orderRepository: OrderRepository,
                     private val userRespository: UserRespository,
                     private val shopRepository: ShopRepository):ViewModel() {


    private val performUpdateProfile = MutableLiveData<Resource<Response<String>>>()
    val performUpdateProfileStatus: LiveData<Resource<Response<String>>>
        get() = performUpdateProfile

    private val orderByIdRequest = MutableLiveData<Resource<Response<OrderItemListModel>>>()
    val orderByIdResponse : LiveData<Resource<Response<OrderItemListModel>>>
    get() = orderByIdRequest

    private val orderByShopId = MutableLiveData<Resource<List<OrderItemListModel>>>()
    val orderByShopIdResponse : LiveData<Resource<List<OrderItemListModel>>>
    get() = orderByShopId

    private val updateOrder = MutableLiveData<Resource<Response<String>>>()
    val updateOrderResponse: LiveData<Resource<Response<String>>>
    get() = updateOrder

    private val orderByPagination = MutableLiveData<Resource<Response<List<OrderItemListModel>>>>()
    val orderByPaginationResponse : LiveData<Resource<Response<List<OrderItemListModel>>>>
        get() = orderByPagination

    private val getShopDetail = MutableLiveData<Resource<Response<ShopConfigurationModel>>>()
    val getShopDetailResponse : LiveData<Resource<Response<ShopConfigurationModel>>>
        get() = getShopDetail

    fun getOrderById(orderId: Int){
        viewModelScope.launch {
            try {
                orderByIdRequest.value = Resource.loading()
                val response = orderRepository.getOrderById(orderId)
                if(response.code==1)
                    orderByIdRequest.value=Resource.success(response)
                else{
                    orderByIdRequest.value=Resource.error(message = response.message)
                }
            }catch (e: Exception){
                if (e is UnknownHostException) {
                    orderByIdRequest.value = Resource.offlineError()
                } else {
                    orderByIdRequest.value = Resource.error(e)
                }
            }
        }
    }



    fun getOrderByShopId(shopId: Int){
        viewModelScope.launch {
            try{
                orderByShopId.value = Resource.loading()
                val response = orderRepository.getOrderByShopId(shopId)

                if(!response.data.isNullOrEmpty()) {
                    var orders = response.data
                    orderByShopId.value = Resource.success(orders)
                }
                else {
                    orderByShopId.value = Resource.empty()
                }

            }catch (e: Exception){
                if (e is UnknownHostException) {
                    orderByShopId.value = Resource.offlineError()
                } else {
                    orderByShopId.value = Resource.error(e)
                }
            }
        }
    }

    fun getOrderByPagination(shopId: Int,pageNum: Int,pageCnt: Int){
        viewModelScope.launch {
            try{
                orderByPagination.value = Resource.loading()

                val response = orderRepository.getOrderByPagination(shopId,pageNum,pageCnt)
                if(!response.data.isNullOrEmpty())
                    orderByPagination.value = Resource.success(response)
                else{
                    orderByPagination.value = Resource.empty()
                }
            }catch (e: Exception){
                if (e is UnknownHostException) {
                    orderByPagination.value = Resource.offlineError()
                } else {
                    orderByPagination.value = Resource.error(e)
                }
            }
        }
    }

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

    fun getShopDetail(id: Int){
        viewModelScope.launch {
            try{
                getShopDetail.value = Resource.loading()
                val response = shopRepository.getShopDetailsById(id)
                if(response.code == 1)
                    getShopDetail.value =Resource.success(response)
                else
                    getShopDetail.value = Resource.error(message = response.message)
            }catch (e: Exception) {
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