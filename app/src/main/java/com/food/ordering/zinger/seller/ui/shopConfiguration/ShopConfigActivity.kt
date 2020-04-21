package com.food.ordering.zinger.seller.ui.shopConfiguration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.ConfigurationModel
import com.food.ordering.zinger.seller.data.model.ShopModel
import com.food.ordering.zinger.seller.databinding.ActivityMenuBinding
import com.food.ordering.zinger.seller.databinding.ActivityShopConfigurationBinding
import com.food.ordering.zinger.seller.ui.menu.MenuViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ShopConfigActivity : AppCompatActivity() {


    private lateinit var binding: ActivityShopConfigurationBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: ShopConfigViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_shop_configuration)
        initView()
        setListeners()
        setObservers()
    }

    private fun initView(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shop_configuration)
    }

    private fun setListeners(){
        binding.btnUpdateItem.setOnClickListener(View.OnClickListener { v->

            var coverUrl = ArrayList<String>()
            coverUrl.add("www.test1.com")
            coverUrl.add("www.test2.com")
            var shopModel = ShopModel("23:00:00",coverUrl,1,"9176019334","Sathyas Main Canteen","08:00:00","www.url.com",null)
            var shopConfig = ConfigurationModel(10.0,1,1,"HARSHA_MID",shopModel)
            viewModel.updateShopConfiguration(shopConfig);
        })
    }


    private fun setObservers(){
        viewModel.shopConfigUpdateResponse.observe(this, Observer { resources ->
            println(resources)
            Toast.makeText(this,"response received", Toast.LENGTH_LONG).show()
        })
    }


}
