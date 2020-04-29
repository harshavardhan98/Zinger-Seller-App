package com.food.ordering.zinger.seller.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.ItemModel
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.retrofit.ItemRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class MenuViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    private val menuRequest = MutableLiveData<Resource<Response<List<ItemModel>>>>()
    val menuRequestResponse: LiveData<Resource<Response<List<ItemModel>>>>
        get() = menuRequest

    fun getMenu(shopId: Int) {
        viewModelScope.launch {
            try {
                menuRequest.value = Resource.loading()
                val response = itemRepository.getShopMenu(shopId)

                if (!response.data.isNullOrEmpty()) {
                    menuRequest.value = Resource.success(response)
                } else {
                    menuRequest.value = Resource.empty()
                }

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    menuRequest.value = Resource.offlineError()
                } else {
                    menuRequest.value = Resource.error(e)
                }
            }
        }
    }
}