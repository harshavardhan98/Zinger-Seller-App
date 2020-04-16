package com.food.ordering.zinger.seller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.seller.databinding.ActivityMainBinding
import com.food.ordering.zinger.seller.ui.menu.MenuActivity
import com.food.ordering.zinger.seller.ui.order.OrderActivity
import com.food.ordering.zinger.seller.ui.seller.SellerActivity
import com.food.ordering.zinger.seller.ui.shopConfiguration.ShopConfigActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        setListeners()
    }

    fun initView(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
    }

    fun setListeners(){

        binding.btnOrder.setOnClickListener(View.OnClickListener { v->
            startActivity(Intent(this,OrderActivity::class.java))
        })

        binding.btnMenu.setOnClickListener(View.OnClickListener { v->
            startActivity(Intent(this,MenuActivity::class.java))
        })

        binding.btnShopConfig.setOnClickListener(View.OnClickListener { v->
            startActivity(Intent(this,ShopConfigActivity::class.java))
        })

        binding.btnSeller.setOnClickListener(View.OnClickListener { v->
            startActivity(Intent(this,SellerActivity::class.java))
        })


    }
}
