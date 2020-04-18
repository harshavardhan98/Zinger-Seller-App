package com.food.ordering.zinger.seller.ui.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.ItemModel
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
            var item1 = ItemModel(name = "Egg Macroni1",price=30.0,photoUrl = "www.photo.com",category = "italian",shopModel = shop,isVeg = 0,isAvailable = null)
            var item2 = ItemModel(name = "Egg Macroni2",price=30.0,photoUrl = "www.photo.com",category = "italian",shopModel = shop,isVeg = 0,isAvailable = null)
            var itemModelList = ArrayList<ItemModel>()
            itemModelList.add(item1)
            itemModelList.add(item2)
            viewModel.addItem(itemModelList)
        })

        binding.btnUpdateItem.setOnClickListener(View.OnClickListener { v->
            var shop = ShopModel(id = 1)
            var item = ItemModel(id = 43,name = "Egg Macroni",price=30.0,photoUrl = "www.photo.com",category = "italian",shopModel = shop,isVeg = 0,isAvailable = 0)
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
