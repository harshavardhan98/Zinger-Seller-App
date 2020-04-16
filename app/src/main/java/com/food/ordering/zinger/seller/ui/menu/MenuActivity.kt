package com.food.ordering.zinger.seller.ui.menu

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.Item
import com.food.ordering.zinger.seller.data.model.ShopModel
import com.food.ordering.zinger.seller.databinding.ActivityMenuBinding
import org.koin.android.ext.android.inject

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: MenuViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_menu)
        initView()
        setListener()
        setObservers()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
    }

    private fun setListener(){

        binding.btnMenu.setOnClickListener(View.OnClickListener { v ->
            viewModel.getMenu(1)
        })

        binding.btnAddItem.setOnClickListener(View.OnClickListener { v ->
            var shop = ShopModel(id = 1)
            var item = Item(name = "Egg Macroni",price=30.0,photoUrl = "www.photo.com",category = "italian",shopModel = shop,isVeg = 0,isAvailable = null)
            viewModel.addItem(item)
        })

        binding.btnUpdateItem.setOnClickListener(View.OnClickListener { v->
            var shop = ShopModel(id = 1)
            var item = Item(id = 43,name = "Egg Macroni",price=30.0,photoUrl = "www.photo.com",category = "italian",shopModel = shop,isVeg = 0,isAvailable = 0)
            viewModel.updateItem(item)
        })

        binding.btnDeleteItem.setOnClickListener(View.OnClickListener { v->
            viewModel.deleteItem(43)
        })

        binding.btnUndeleteItem.setOnClickListener(View.OnClickListener { v->
            viewModel.unDeleteItem(43)
        })
    }

    private fun setObservers(){
        viewModel.menuRequestResponse.observe(this, Observer { resource ->
            println(resource)
            Toast.makeText(this,"response received",Toast.LENGTH_LONG).show()
        })

        viewModel.addItemResponse.observe(this, Observer { resource ->
            println(resource)
            Toast.makeText(this,"response received",Toast.LENGTH_LONG).show()
        })

        viewModel.updateItemResponse.observe(this, Observer { resource ->
            println(resource)
            Toast.makeText(this,"response received",Toast.LENGTH_LONG).show()
        })

        viewModel.deleteItemResponse.observe(this, Observer { resource ->
            println(resource)
            Toast.makeText(this,"response received",Toast.LENGTH_LONG).show()
        })

        viewModel.unDeleteItemResponse.observe(this, Observer { resource ->
            println(resource)
            Toast.makeText(this,"response received",Toast.LENGTH_LONG).show()
        })
    }

}
