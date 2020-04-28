package com.food.ordering.zinger.seller.ui.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.model.UserInviteModel
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.data.model.UserShopModel
import com.food.ordering.zinger.seller.data.retrofit.SellerRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

import kotlin.Exception

class SellerViewModel(private val sellerRepository: SellerRepository) : ViewModel() {

    private val getSeller = MutableLiveData<Resource<Response<List<UserModel>>>>()
    val getSellerResponse: LiveData<Resource<Response<List<UserModel>>>>
        get() = getSeller


    private val inviteSeller = MutableLiveData<Resource<Response<String>>>()
    val inviteSellerResponse: LiveData<Resource<Response<String>>>
        get() = inviteSeller


    private val verifyInvite = MutableLiveData<Resource<Response<UserInviteModel>>>()
    val verifyInviteResponse: LiveData<Resource<Response<UserInviteModel>>>
        get() = verifyInvite

    private val acceptInvite = MutableLiveData<Resource<Response<String>>>()
    val acceptInviteResponse: LiveData<Resource<Response<String>>>
        get() = acceptInvite

    private val deleteInvite = MutableLiveData<Resource<Response<String>>>()
    val deleteInviteResponse: LiveData<Resource<Response<String>>>
        get() = deleteInvite

    private val notifyInvite = MutableLiveData<Resource<Response<String>>>()
    val notifyInviteResponse: LiveData<Resource<Response<String>>>
        get() = notifyInvite


    fun getSeller(shopId: String) {
        viewModelScope.launch {
            try {
                getSeller.value = Resource.loading()
                val response = sellerRepository.getSeller(shopId)
                if (!response.data.isNullOrEmpty())
                    getSeller.value = Resource.success(response)
                else
                    getSeller.value = Resource.empty()
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    getSeller.value = Resource.offlineError()
                } else {
                    getSeller.value = Resource.error(e)
                }
            }
        }
    }

    fun inviteSeller(userShopModel: UserShopModel) {
        viewModelScope.launch {
            try {
                inviteSeller.value = Resource.loading()
                val response = sellerRepository.inviteSeller(userShopModel)

                if (response.code == 1)
                    inviteSeller.value = Resource.success(response)
                else
                    inviteSeller.value = Resource.error(message = response.message)

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    inviteSeller.value = Resource.offlineError()
                } else {
                    inviteSeller.value = Resource.error(e)
                }
            }
        }
    }

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

    fun acceptInvite(userShop: UserShopModel) {

        viewModelScope.launch {
            try {
                acceptInvite.value = Resource.loading()
                val response = sellerRepository.acceptInvite(userShop)

                if (response.code == 1)
                    acceptInvite.value = Resource.success(response)
                else
                    acceptInvite.value = Resource.error(message = response.message)

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    acceptInvite.value = Resource.offlineError()
                } else {
                    acceptInvite.value = Resource.error(e)
                }
            }
        }

    }

    fun deleteInvite(userShop: UserShopModel) {

        viewModelScope.launch {
            try {
                deleteInvite.value = Resource.loading()
                val response = sellerRepository.deleteInvite(userShop)

                if (response.code == 1)
                    deleteInvite.value = Resource.success(response)
                else
                    deleteInvite.value = Resource.error(message = response.message)

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    deleteInvite.value = Resource.offlineError()
                } else {
                    deleteInvite.value = Resource.error(e)
                }
            }
        }
    }

    fun notifyInvite(userShop: UserShopModel) {

        viewModelScope.launch {
            try {
                notifyInvite.value = Resource.loading()
                val response = sellerRepository.notifyInvite(userShop)

                if (response.code == 1)
                    notifyInvite.value = Resource.success(response)
                else
                    notifyInvite.value = Resource.error(message = response.message)

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    notifyInvite.value = Resource.offlineError()
                } else {
                    notifyInvite.value = Resource.error(e)
                }
            }
        }

    }



}