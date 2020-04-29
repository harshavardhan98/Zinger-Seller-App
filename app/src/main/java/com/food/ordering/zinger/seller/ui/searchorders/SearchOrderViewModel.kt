package com.food.ordering.zinger.seller.ui.searchorders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.OrderItemListModel
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class SearchOrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val searchOrder = MutableLiveData<Resource<Response<List<OrderItemListModel>>>>()
    val searchOrderResponse: LiveData<Resource<Response<List<OrderItemListModel>>>>
        get() = searchOrder

    fun getOrderBySearchTerm(shopId: Int,searchTerm: String, pageNum: Int, pageCnt: Int) {
        viewModelScope.launch {
            try {
                searchOrder.value = Resource.loading()
                val response = orderRepository.getOrderBySearchItem(shopId,searchTerm, pageNum, pageCnt)
                if (response.code == 1)
                    searchOrder.value = Resource.success(response)
                else {
                    searchOrder.value = Resource.error(message = response.message)
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    searchOrder.value = Resource.offlineError()
                } else {
                    searchOrder.value = Resource.error(e)
                }
            }
        }
    }
}