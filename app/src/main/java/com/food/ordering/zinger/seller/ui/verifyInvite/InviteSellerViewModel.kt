package com.food.ordering.zinger.seller.ui.verifyInvite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.model.UserInviteModel
import com.food.ordering.zinger.seller.data.retrofit.SellerRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class InviteSellerViewModel(private val sellerRepository: SellerRepository) : ViewModel() {

    private val verifyInvite = MutableLiveData<Resource<Response<UserInviteModel>>>()
    val verifyInviteResponse: LiveData<Resource<Response<UserInviteModel>>>
        get() = verifyInvite

    fun verifyInvite(shopId: Int,mobile: String) {
        viewModelScope.launch {
            try {
                verifyInvite.value = Resource.loading()
                val response = sellerRepository.verifyInvite(shopId,mobile)

                if (response.code == 1)
                    verifyInvite.value = Resource.success(response)
                else
                    verifyInvite.value = Resource.error(message = response.message)

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    verifyInvite.value = Resource.offlineError()
                } else {
                    verifyInvite.value = Resource.error(e)
                }
            }
        }
    }



}