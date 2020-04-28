package com.food.ordering.zinger.seller.ui.orderDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.OrderItemListModel
import com.food.ordering.zinger.seller.data.model.OrderModel
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.retrofit.OrderRepository
import kotlinx.coroutines.launch

class OrderDetailViewModel(private val orderRepository: OrderRepository):ViewModel(){

    private val orderByIdRequest = MutableLiveData<Resource<Response<OrderItemListModel>>>()
    val orderByIdResponse : LiveData<Resource<Response<OrderItemListModel>>>
        get() = orderByIdRequest

    private val updateOrder = MutableLiveData<Resource<Response<String>>>()
    val updateOrderResponse: LiveData<Resource<Response<String>>>
        get() = updateOrder


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
                println(e.printStackTrace())
            }
        }
    }

    fun updateOrder(orderModel: OrderModel){
        viewModelScope.launch {
            try{
                updateOrder.value = Resource.loading()
                val response = orderRepository.updateOrderStatus(orderModel)

                if(response.code==1) {
                    updateOrder.value = Resource.success(response)
                }
                else{
                    println("Something is wrong")
                    updateOrder.value = Resource.error(message=response.message)
                }
            }catch (e: Exception){
                println(e.printStackTrace())
            }
        }
    }


}