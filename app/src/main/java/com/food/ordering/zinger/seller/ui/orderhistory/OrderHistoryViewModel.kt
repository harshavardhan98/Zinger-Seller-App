package com.food.ordering.zinger.seller.ui.orderhistory

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

class OrderHistoryViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val orderByPagination = MutableLiveData<Resource<Response<List<OrderItemListModel>>>>()
    val orderByPaginationResponse: LiveData<Resource<Response<List<OrderItemListModel>>>>
        get() = orderByPagination

    fun getOrderByPagination(shopId: Int, pageNum: Int, pageCnt: Int) {
        viewModelScope.launch {
            try {
                orderByPagination.value = Resource.loading()

                val response = orderRepository.getOrderByPagination(shopId, pageNum, pageCnt)
                if (!response.data.isNullOrEmpty())
                    orderByPagination.value = Resource.success(response)
                else {
                    orderByPagination.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    orderByPagination.value = Resource.offlineError()
                } else {
                    orderByPagination.value = Resource.error(e)
                }
            }
        }
    }

    /*****************************************************************************/


}