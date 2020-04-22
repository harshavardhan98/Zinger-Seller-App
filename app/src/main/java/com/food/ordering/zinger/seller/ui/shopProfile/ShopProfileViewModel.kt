package com.food.ordering.zinger.seller.ui.shopProfile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.*
import com.food.ordering.zinger.seller.data.retrofit.ShopRepository
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class ShopProfileViewModel(private val shopRepository: ShopRepository) : ViewModel() {

    private val performUpdateShopProfile = MutableLiveData<Resource<Response<String>>>()
    val performUpdateShopProfileStatus: LiveData<Resource<Response<String>>>
        get() = performUpdateShopProfile

    private val performUploadImage = MutableLiveData<Resource<String>>()
    val performUploadImageStatus: LiveData<Resource<String>>
        get() = performUploadImage

    fun updateShopProfile(shopConfigRequest: ConfigurationModel) {
        viewModelScope.launch {
            try {
                performUpdateShopProfile.value = Resource.loading()
                val response = shopRepository.updateShopConfiguration(shopConfigRequest)
                if (response.code == 1)
                    performUpdateShopProfile.value = Resource.success(response)
                else
                    performUpdateShopProfile.value = Resource.error(message = response.message)
            } catch (e: Exception) {
                println("fetch stats failed ${e.message}")
                if (e is UnknownHostException) {
                    performUpdateShopProfile.value = Resource.offlineError()
                } else {
                    performUpdateShopProfile.value = Resource.error(e)
                }
            }
        }
    }

    fun uploadPhotoToFireBase(storageReference: StorageReference,uri: Uri){

        viewModelScope.launch {
            try{
                performUploadImage.value = Resource.loading()
                storageReference.putFile(uri)
                    .addOnSuccessListener{
                        val result = it.metadata!!.reference!!.downloadUrl;
                        result.addOnSuccessListener {
                            val imageLink = it.toString()
                            performUploadImage.value = Resource.success(imageLink)
                        }
                    }
                    .addOnFailureListener {
                            performUploadImage.value = Resource.error(message = "Error updating photo")
                    }

            }catch (e: Exception){
                if (e is UnknownHostException) {
                    performUploadImage.value = Resource.offlineError()
                } else {
                    performUploadImage.value = Resource.error(e)
                }

            }
        }

    }
}