package com.food.ordering.zinger.seller.ui.shopConfiguration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.ConfigurationModel
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.retrofit.ShopRepository
import kotlinx.coroutines.launch

class ShopConfigViewModel(private val shopRepository: ShopRepository):ViewModel() {


    private val shopConfigUpdateRequest = MutableLiveData<Resource<Response<String>>>()
    val shopConfigUpdateResponse : LiveData<Resource<Response<String>>>
        get() = shopConfigUpdateRequest


    fun updateShopConfiguration(shopConfig: ConfigurationModel){

        viewModelScope.launch {
            try{
                shopConfigUpdateRequest.value =Resource.loading()
                val response = shopRepository.updateShopConfiguration(shopConfig)
                if(response.code == 1)
                    shopConfigUpdateRequest.value = Resource.success(response)
                else
                    println("wrong: "+response)
            }catch (e:Exception){
                println(e.printStackTrace())
            }
        }
    }

}