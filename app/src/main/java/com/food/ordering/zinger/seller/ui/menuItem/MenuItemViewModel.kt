package com.food.ordering.zinger.seller.ui.menuItem

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.ItemModel
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.retrofit.ItemRepository
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class MenuItemViewModel(private val itemRepository: ItemRepository) : ViewModel() {


    private val performUploadImage = MutableLiveData<Resource<String>>()
    val performUploadImageStatus: LiveData<Resource<String>>
        get() = performUploadImage

    fun uploadPhotoToFireBase(storageReference: StorageReference, uri: Uri) {

        viewModelScope.launch {
            try {
                performUploadImage.value = Resource.loading()
                storageReference.putFile(uri)
                    .addOnSuccessListener {
                        val result = it.metadata!!.reference!!.downloadUrl;
                        result.addOnSuccessListener {
                            val imageLink = it.toString()
                            performUploadImage.value = Resource.success(imageLink)
                        }
                    }
                    .addOnFailureListener {
                        performUploadImage.value = Resource.error(message = "Error updating photo")
                    }

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    performUploadImage.value = Resource.offlineError()
                } else {
                    performUploadImage.value = Resource.error(e)
                }

            }
        }

    }


    /*********************************************************************************************/

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

    /*********************************************************************************************/

    private val addItem = MutableLiveData<Resource<Response<String>>>()
    val addItemResponse: LiveData<Resource<Response<String>>>
        get() = addItem

    fun addItem(item: List<ItemModel>) {
        viewModelScope.launch {
            try {
                addItem.value = Resource.loading()
                val response = itemRepository.addItem(item)
                if (response.code == 1) {
                    addItem.value = Resource.success(response)
                } else
                    addItem.value = Resource.error(message = response.message)
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    addItem.value = Resource.offlineError()
                } else {
                    addItem.value = Resource.error(e)
                }
            }
        }
    }


    /*********************************************************************************************/


    private val updateItem = MutableLiveData<Resource<Response<String>>>()
    val updateItemResponse: LiveData<Resource<Response<String>>>
        get() = updateItem

    fun updateItem(item: List<ItemModel>) {
        viewModelScope.launch {
            try {
                updateItem.value = Resource.loading()
                val response = itemRepository.updateItem(item)
                if (response.code == 1)
                    updateItem.value = Resource.success(response)
                else
                    updateItem.value = Resource.error(message = response.message)
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    updateItem.value = Resource.offlineError()
                } else {
                    updateItem.value = Resource.error(e)
                }
            }
        }
    }

    /*********************************************************************************************/

    private val deleteItem = MutableLiveData<Resource<Response<String>>>()
    val deleteItemResponse: LiveData<Resource<Response<String>>>
        get() = deleteItem

    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            try {
                deleteItem.value = Resource.loading()
                val response = itemRepository.deleteItem(itemId)

                if (response.code == 1)
                    deleteItem.value = Resource.success(response)
                else
                    deleteItem.value = Resource.error(message = response.message)

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    deleteItem.value = Resource.offlineError()
                } else {
                    deleteItem.value = Resource.error(e)
                }
            }
        }
    }
}