package com.food.ordering.zinger.seller.ui.orderDetail

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.OrderItemListModel
import com.food.ordering.zinger.seller.data.model.OrderItems
import com.food.ordering.zinger.seller.databinding.ActivityOrderDetailBinding
import com.food.ordering.zinger.seller.ui.order.OrderViewModel
import com.food.ordering.zinger.seller.utils.AppConstants
import com.food.ordering.zinger.seller.utils.AppConstants.REQUEST_PHONE_CALL
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class OrderDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOrderDetailBinding
    private val viewModel: OrderViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var orderAdapter: OrderItemAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderItems> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private lateinit var order: OrderItemListModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo check if delivery location and delivery price are set for delivery orders


        getArgs()
        initView()
        setListeners()
        errorSnackBar.setAction("Try again") {
        }
    }

    private fun getArgs(){
        order = Gson().fromJson(intent.getStringExtra(AppConstants.ORDER_DETAIL), OrderItemListModel::class.java)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        setupShopRecyclerView()
        updateUI()
    }

    private fun updateUI(){
        binding.textUserName.text = order.transactionModel.orderModel.userModel?.name
        try {
            val appDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aaa")
            val dateString = appDateFormat.format(order.transactionModel.orderModel.date)
            // todo UI does not have date
            //binding.textOrderTime.text = dateString
        }catch (e: Exception){
            e.printStackTrace()
        }
        //binding.textOrderPrice.text = "₹ " + order.transactionModel.orderModel.price.toInt().toString()

        binding.textOrderId.text = "#"+order.transactionModel.orderModel.id
        binding.textTransactionId.text = "#"+order.transactionModel.transactionId
        binding.textTotalPrice.text = "₹"+ order.transactionModel.orderModel.price?.toInt().toString()
        binding.textPaymentMode.text = "Paid via "+order.transactionModel.paymentMode
        if(!order.transactionModel.orderModel.cookingInfo.isNullOrEmpty()){
            binding.textInfo.text = order.transactionModel.orderModel.cookingInfo
        }else{
            binding.textInfo.visibility = View.GONE
        }
        if(!order.transactionModel.orderModel.deliveryLocation.isNullOrEmpty()){
            binding.textDeliveryLocation.text = order.transactionModel.orderModel.deliveryLocation
        }else{
            binding.textDeliveryLocation.text = "Pick up from restaurant"
        }
        var itemTotal = 0.0
        order.orderItemsList.forEach {
            itemTotal+=it.price
        }
        binding.textItemTotalPrice.text = "₹"+itemTotal.toInt().toString()
        if(order.transactionModel.orderModel.deliveryPrice!=null){
            if(order.transactionModel.orderModel.deliveryPrice!!>0.0){
                binding.textDeliveryPrice.text = "₹"+ order.transactionModel.orderModel.deliveryPrice!!.toInt().toString()
            }else{
                binding.layoutDeliveryCharge.visibility = View.GONE
            }
        }else{
            binding.layoutDeliveryCharge.visibility = View.GONE
        }

        if(order.transactionModel.orderModel.rating!=null){
            if(order.transactionModel.orderModel.rating!!>0.0){
                binding.layoutRating.visibility = View.VISIBLE
                binding.textRating.text = order.transactionModel.orderModel.rating.toString()
            }
        }

    }

    private fun setListeners() {
        binding.imageCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + order.transactionModel.orderModel.userModel?.mobile))
            startActivity(intent)
        }
    }


    private fun setupShopRecyclerView() {
        orderList.addAll(order.orderItemsList)
        orderAdapter = OrderItemAdapter(applicationContext, orderList, object : OrderItemAdapter.OnItemClickListener {
            override fun onItemClick(item: OrderItems?, position: Int) {
                //val intent = Intent(applicationContext, RestaurantActivity::class.java)
                //intent.putExtra("shop", item)
                //startActivity(intent)
            }
        })
        binding.recyclerOrderItems.layoutManager = LinearLayoutManager(this@OrderDetailActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerOrderItems.adapter = orderAdapter
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_close -> {
                onBackPressed()
            }
        }
    }
}
