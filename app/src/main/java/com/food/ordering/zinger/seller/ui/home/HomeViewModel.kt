package com.food.ordering.zinger.seller.ui.home

import androidx.lifecycle.*
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.OrderModel
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.retrofit.OrderRepository
import com.food.ordering.zinger.seller.data.retrofit.ShopRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException


class HomeViewModel(private val orderRepository: OrderRepository,private val preferencesHelper: PreferencesHelper) : ViewModel() {

    private val updateOrder = MutableLiveData<Resource<Response<String>>>()
    val updateOrderResponse: LiveData<Resource<Response<String>>>
        get() = updateOrder

    fun updateOrder(orderModel: OrderModel){
        viewModelScope.launch {
            try{
                updateOrder.value = Resource.loading()
                val response = orderRepository.updateOrderStatus(orderModel)

                if(response.code==1) {
                    updateOrder.value = Resource.success(response)
                    preferencesHelper.orderStatusChanged = true
                }
                else{
                    updateOrder.value = Resource.error(message=response.message)
                }
            }catch (e: Exception){
                if (e is UnknownHostException) {
                    updateOrder.value = Resource.offlineError()
                } else {
                    updateOrder.value = Resource.error(e)
                }
            }
        }
    }
}