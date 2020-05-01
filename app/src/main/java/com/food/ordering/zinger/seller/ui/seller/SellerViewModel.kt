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

    /*****************************************************************************/


    private val inviteSeller = MutableLiveData<Resource<Response<String>>>()
    val inviteSellerResponse: LiveData<Resource<Response<String>>>
        get() = inviteSeller

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

    /*****************************************************************************/


    private val deleteInvite = MutableLiveData<Resource<Response<String>>>()
    val deleteInviteResponse: LiveData<Resource<Response<String>>>
        get() = deleteInvite
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

    /*****************************************************************************/

    private val notifyInvite = MutableLiveData<Resource<Response<String>>>()
    val notifyInviteResponse: LiveData<Resource<Response<String>>>
        get() = notifyInvite

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

    /*****************************************************************************/

    private val deleteSeller = MutableLiveData<Resource<Response<String>>>()
    val deleteSellerResponse: LiveData<Resource<Response<String>>>
        get() = deleteSeller

    fun deleteSeller(shopId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                deleteSeller.value = Resource.loading()
                val response = sellerRepository.deleteSeller(shopId, userId)
                if (response.code == 1)
                    deleteSeller.value = Resource.success(response)
                else
                    deleteSeller.value = Resource.error(message = response.message)

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    deleteSeller.value = Resource.offlineError()
                } else {
                    deleteSeller.value = Resource.error(e)
                }
            }
        }
    }

    /*****************************************************************************/

}