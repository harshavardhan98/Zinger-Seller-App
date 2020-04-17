package com.food.ordering.zinger.seller.ui.login
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.Response
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.data.model.UserShopListModel
import com.food.ordering.zinger.seller.data.retofit.UserRespository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class LoginViewModel(private val userRespository: UserRespository):ViewModel() {

}