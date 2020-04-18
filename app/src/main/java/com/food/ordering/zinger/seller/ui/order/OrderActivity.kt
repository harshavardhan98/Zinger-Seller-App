package com.food.ordering.zinger.seller.ui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.OrderModel
import com.food.ordering.zinger.seller.databinding.ActivityOrderBinding
import com.food.ordering.zinger.seller.databinding.ActivityShopConfigurationBinding
import com.food.ordering.zinger.seller.ui.shopConfiguration.ShopConfigViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class OrderActivity : AppCompatActivity() {


    private lateinit var binding: ActivityOrderBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: OrderViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_order)
        initView()
        setListeners()
        setObservers()
    }

    private fun initView(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_order)
    }

    private fun setListeners(){

        binding.btnOrderId.setOnClickListener(View.OnClickListener { v ->
            viewModel.getOrderById(4)
        })

        binding.btnCurrentOrders.setOnClickListener(View.OnClickListener { v ->
            viewModel.getOrderByShopId(1)
        })

        binding.btnUpdateOrderStatus.setOnClickListener(View.OnClickListener { V ->
            var orderModel = OrderModel(id=4,orderStatus = "ACCEPTED")
            viewModel.updateOrder(orderModel)
        })

        binding.btnPastOrders.setOnClickListener(View.OnClickListener { V ->
            viewModel.getOrderByPagination(1,1,2)
        })



    }

    private fun setObservers(){

        viewModel.orderByIdResponse.observe(this, Observer { resources ->
            println(resources.toString())
            Toast.makeText(this,"response received",Toast.LENGTH_LONG).show()
        })

        viewModel.orderByShopIdResponse.observe(this,Observer{resources ->
            println(resources.toString())
            Toast.makeText(this,"response received",Toast.LENGTH_LONG).show()
        })

        viewModel.updateOrderResponse.observe(this, Observer { resources ->
            println(resources.toString())
            Toast.makeText(this,"response received",Toast.LENGTH_LONG).show()
        })

        viewModel.orderByPaginationResponse.observe(this, Observer { resources ->
            println("Paginated Result: \n"+resources.toString())
            Toast.makeText(this,"response received",Toast.LENGTH_LONG).show()
        })
    }

}
