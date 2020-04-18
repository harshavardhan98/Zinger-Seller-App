package com.food.ordering.zinger.seller.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.ItemModel
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.retofit.ItemRepository
import kotlinx.coroutines.launch

class MenuViewModel(private val itemRepository: ItemRepository): ViewModel() {

    private val menuRequest = MutableLiveData<Resource<Response<List<ItemModel>>>>()
    val menuRequestResponse : LiveData<Resource<Response<List<ItemModel>>>>
    get() = menuRequest

    private val addItem = MutableLiveData<Resource<Response<String>>>()
    val addItemResponse : LiveData<Resource<Response<String>>>
    get() = addItem

    private val updateItem = MutableLiveData<Resource<Response<String>>>()
    val updateItemResponse : LiveData<Resource<Response<String>>>
        get() = updateItem

    private val deleteItem = MutableLiveData<Resource<Response<String>>>()
    val deleteItemResponse : LiveData<Resource<Response<String>>>
        get() = deleteItem

    private val unDeleteItem = MutableLiveData<Resource<Response<String>>>()
    val unDeleteItemResponse : LiveData<Resource<Response<String>>>
        get() = unDeleteItem

    fun getMenu(shopId: Int){
        viewModelScope.launch {
            try{
                menuRequest.value = Resource.loading()
                val response = itemRepository.getShopMenu(shopId)
                if(response.code==1)
                    menuRequest.value =Resource.success(response)
                else
                    System.out.println("wrong: "+response)
            }catch (e: Exception){
                println(e.printStackTrace())
            }
        }
    }

    fun addItem(item: List<ItemModel>){
        viewModelScope.launch {
            try{
                addItem.value = Resource.loading()
                val response = itemRepository.addItem(item)
                if(response.code==1)
                    addItem.value = Resource.success(response)
                else
                    System.out.println("wrong: "+response)
            }catch (e: Exception){
                println(e.printStackTrace())
            }
        }
    }

    fun updateItem(item: ItemModel){
        viewModelScope.launch {
            try {
                updateItem.value = Resource.loading()
                val response = itemRepository.updateItem(item)
                if(response.code==1)
                    updateItem.value = Resource.success(response)
                else
                    System.out.println("wrong: "+response)

            }catch (e: Exception){
                println(e.printStackTrace())
            }
        }
    }

    fun deleteItem(itemId: Int){
        viewModelScope.launch {
            try{
                deleteItem.value = Resource.loading()
                val response = itemRepository.deleteItem(itemId)

                if(response.code==1)
                    deleteItem.value = Resource.success(response)
                else
                    System.out.println("wrong: "+response)

            }catch (e: Exception){
                println(e.printStackTrace())
            }
        }
    }

    fun unDeleteItem(itemId: Int){
        viewModelScope.launch {
            try{
                unDeleteItem.value = Resource.loading()
                val response = itemRepository.unDeleteItem(itemId)

                if(response.code==1)
                    unDeleteItem.value = Resource.success(response)
                else
                    println("wrong: "+response)

            }catch (e: Exception){
                println(e.printStackTrace())
            }
        }

    }



}