package com.food.ordering.zinger.seller.ui.seller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.ShopModel
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.data.model.UserShopModel
import com.food.ordering.zinger.seller.databinding.ActivitySellerBinding
import com.food.ordering.zinger.seller.databinding.ActivityShopConfigurationBinding
import kotlinx.android.synthetic.main.activity_seller.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SellerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellerBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: SellerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)

        initView()
        setListeners()
        setObservers()
    }

    private fun initView(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_seller)
    }

    private fun setListeners(){

        binding.btnGetSeller.setOnClickListener(View.OnClickListener {v ->
            viewModel.getSeller("1")
        })

        binding.btnInviteSeller.setOnClickListener(View.OnClickListener {v ->
            val user = UserModel(mobile="9176786587",role = "SELLER")
            val shop = ShopModel(id = 1)
            val userShop = UserShopModel(shop,user)
            viewModel.inviteSeller(userShop)
        })

        binding.btnVerifyInvite.setOnClickListener(View.OnClickListener {v ->
            viewModel.verifyInvite(1,"9176786587")
        })

        binding.btnAcceptInvite.setOnClickListener(View.OnClickListener {v ->
            val user = UserModel(mobile="9176786587",oauthId = "auth_9176786587")
            val shop = ShopModel(id = 1)
            val userShop = UserShopModel(shop,user)
            viewModel.acceptInvite(userShop)
        })

        binding.btnDeleteInvite.setOnClickListener(View.OnClickListener {v ->
            val user = UserModel(mobile="9176786587",oauthId = "auth_9176786587",role = "SELLER")
            val shop = ShopModel(id = 1)
            val userShop = UserShopModel(shop,user)
            viewModel.deleteInvite(userShop)
        })

        binding.btnNotifyInvite.setOnClickListener(View.OnClickListener {v ->

            val user = UserModel(mobile="9176786587")
            val shop = ShopModel(id = 1)
            val userShop = UserShopModel(shop,user)
            viewModel.notifyInvite(userShop)
        })
    }


    private fun setObservers(){
        viewModel.getSellerResponse.observe(this, Observer { resources ->
            println(resources.toString())
            Toast.makeText(this,"response received", Toast.LENGTH_LONG).show()
        })

        viewModel.inviteSellerResponse.observe(this, Observer { resources ->
            println(resources.toString())
            Toast.makeText(this,"response received", Toast.LENGTH_LONG).show()
        })

        viewModel.verifyInviteResponse.observe(this, Observer { resources ->
            println(resources.toString())
            Toast.makeText(this,"response received", Toast.LENGTH_LONG).show()
        })

        viewModel.acceptInviteResponse.observe(this, Observer { resources ->
            println(resources.toString())
            Toast.makeText(this,"response received", Toast.LENGTH_LONG).show()
        })

        viewModel.deleteInviteResponse.observe(this, Observer { resources ->
            println(resources.toString())
            Toast.makeText(this,"response received", Toast.LENGTH_LONG).show()
        })

        viewModel.notifyInviteResponse.observe(this, Observer { resources ->
            println(resources.toString())
            Toast.makeText(this,"response received", Toast.LENGTH_LONG).show()
        })
    }

}
