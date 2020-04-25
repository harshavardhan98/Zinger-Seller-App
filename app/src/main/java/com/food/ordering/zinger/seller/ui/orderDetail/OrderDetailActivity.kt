package com.food.ordering.zinger.seller.ui.orderDetail

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.OrderItemListModel
import com.food.ordering.zinger.seller.data.model.OrderItems
import com.food.ordering.zinger.seller.data.model.OrderModel
import com.food.ordering.zinger.seller.databinding.ActivityOrderDetailBinding
import com.food.ordering.zinger.seller.databinding.BottomSheetSecretKeyBinding
import com.food.ordering.zinger.seller.ui.order.OrderViewModel
import com.food.ordering.zinger.seller.utils.AppConstants
import com.food.ordering.zinger.seller.utils.AppConstants.REQUEST_PHONE_CALL
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private lateinit var order: OrderItemListModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getArgs()
        initView()
        setListeners()
        setObservers()
    }

    private fun getArgs() {
        order = Gson().fromJson(
            intent.getStringExtra(AppConstants.ORDER_DETAIL),
            OrderItemListModel::class.java
        )
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)


        var status = order.transactionModel.orderModel.orderStatus
        // CANCELLED_BY_SELLER,CANCELLED_BY_USER, DELIVERED, COMPLETED
        var terminalStates = arrayOf<String>(
            AppConstants.STATUS.COMPLETED.name,
            AppConstants.STATUS.DELIVERED.name,
            AppConstants.STATUS.REFUND_INITIATED.name,
            AppConstants.STATUS.REFUND_INITIATED.name,
            AppConstants.STATUS.CANCELLED_BY_USER.name,
            AppConstants.STATUS.CANCELLED_BY_SELLER.name
        )

        if (terminalStates.contains(status)) {
            binding.textCancel.visibility = View.GONE
            binding.textCancel.isEnabled = false
            binding.textUpdateStatus.visibility = View.GONE
            binding.textUpdateStatus.isEnabled = false
        } else if (status == AppConstants.STATUS.OUT_FOR_DELIVERY.name || status == AppConstants.STATUS.READY.name) {
            binding.textCancel.visibility = View.GONE
            binding.textCancel.isEnabled = false
        }

        when (order.transactionModel.orderModel.orderStatus) {
            AppConstants.STATUS.PLACED.name -> {
                binding.textUpdateStatus.text = "ACCEPT"
            }
            AppConstants.STATUS.ACCEPTED.name -> {
                if (order.transactionModel.orderModel.deliveryLocation == null)
                    binding.textUpdateStatus.text = AppConstants.STATUS.READY.name
                else
                    binding.textUpdateStatus.text = AppConstants.STATUS.OUT_FOR_DELIVERY.name
            }
            AppConstants.STATUS.READY.name -> {
                binding.textCancel.visibility = View.INVISIBLE
                binding.textCancel.isEnabled = false
                binding.textUpdateStatus.text = "COMPLETE"
            }
            AppConstants.STATUS.OUT_FOR_DELIVERY.name -> {
                binding.textCancel.visibility = View.INVISIBLE
                binding.textCancel.isEnabled = false
                binding.textUpdateStatus.text = AppConstants.STATUS.DELIVERED.name
            }
        }

        setupShopRecyclerView()
        updateUI()
    }

    private fun updateUI() {
        binding.textUserName.text = order.transactionModel.orderModel.userModel?.name
        try {
            val appDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aaa")
            val dateString = appDateFormat.format(order.transactionModel.orderModel.date)
            binding.textLastUpdateTime.text = dateString
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //binding.textOrderPrice.text = "₹ " + order.transactionModel.orderModel.price.toInt().toString()

        binding.textOrderId.text = "#" + order.transactionModel.orderModel.id
        binding.textTransactionId.text = "#" + order.transactionModel.transactionId
        binding.textTotalPrice.text =
            "₹" + order.transactionModel.orderModel.price?.toInt().toString()
        binding.textPaymentMode.text = "Paid via " + order.transactionModel.paymentMode
        if (!order.transactionModel.orderModel.cookingInfo.isNullOrEmpty()) {
            binding.textInfo.text = order.transactionModel.orderModel.cookingInfo
        } else {
            binding.textInfo.visibility = View.GONE
        }
        if (!order.transactionModel.orderModel.deliveryLocation.isNullOrEmpty()) {
            binding.textDeliveryLocation.text = order.transactionModel.orderModel.deliveryLocation
        } else {
            binding.textDeliveryLocation.text = "Pick up from restaurant"
        }
        var itemTotal = 0.0
        order.orderItemsList.forEach {
            itemTotal += it.price
        }
        binding.textItemTotalPrice.text = "₹" + itemTotal.toInt().toString()
        if (order.transactionModel.orderModel.deliveryPrice != null) {
            if (order.transactionModel.orderModel.deliveryPrice!! > 0.0) {
                binding.textDeliveryPrice.text =
                    "₹" + order.transactionModel.orderModel.deliveryPrice!!.toInt().toString()
            } else {
                binding.layoutDeliveryCharge.visibility = View.GONE
            }
        } else {
            binding.layoutDeliveryCharge.visibility = View.GONE
        }

        binding.layoutRating.visibility = View.INVISIBLE
        if (order.transactionModel.orderModel.rating != null) {
            if (order.transactionModel.orderModel.rating!! > 0.0) {
                binding.layoutRating.visibility = View.VISIBLE
                binding.textRating.text = order.transactionModel.orderModel.rating.toString()
            }
        }

    }

    private fun setListeners() {
        binding.imageCall.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + order.transactionModel.orderModel.userModel?.mobile)
            )
            startActivity(intent)
        }

        binding.textCancel.setOnClickListener {
            val orderModel = OrderModel(
                id = order.transactionModel.orderModel.id,
                orderStatus = AppConstants.STATUS.CANCELLED_BY_SELLER.name
            )
            viewModel.updateOrder(orderModel)
        }

        binding.textUpdateStatus.setOnClickListener {
            when (order.transactionModel.orderModel.orderStatus) {

                AppConstants.STATUS.PLACED.name -> {
                    val orderModel = OrderModel(
                        id = order.transactionModel.orderModel.id,
                        orderStatus = AppConstants.STATUS.ACCEPTED.name
                    )
                    viewModel.updateOrder(orderModel)
                }

                AppConstants.STATUS.ACCEPTED.name -> {
                    var orderModel = OrderModel(id = order.transactionModel.orderModel.id)

                    if (order.transactionModel.orderModel.deliveryLocation == null)
                        orderModel.orderStatus = AppConstants.STATUS.READY.name
                    else
                        orderModel.orderStatus = AppConstants.STATUS.OUT_FOR_DELIVERY.name

                    viewModel.updateOrder(orderModel)
                }

                AppConstants.STATUS.READY.name,
                AppConstants.STATUS.OUT_FOR_DELIVERY.name -> {
                    showSecretKeyBottomSheet(order)
                }
            }

        }

    }

    private fun setupShopRecyclerView() {
        orderList.addAll(order.orderItemsList)
        orderAdapter = OrderItemAdapter(
            applicationContext,
            orderList,
            object : OrderItemAdapter.OnItemClickListener {
                override fun onItemClick(item: OrderItems?, position: Int) {
                    //val intent = Intent(applicationContext, RestaurantActivity::class.java)
                    //intent.putExtra("shop", item)
                    //startActivity(intent)
                }
            })
        binding.recyclerOrderItems.layoutManager =
            LinearLayoutManager(this@OrderDetailActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerOrderItems.adapter = orderAdapter
    }

    private fun setObservers() {


        viewModel.orderByIdResponse.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        var intent = getIntent()
                        intent.putExtra(
                            AppConstants.ORDER_DETAIL,
                            Gson().toJson(resource.data?.data)
                        )
                        startActivity(intent)
                        finish()
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Something went wrong. Error:\n" + resource.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Updating orders...")
                        progressDialog.show()
                    }

                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Offline Error", Toast.LENGTH_LONG).show()
                    }

                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Empty Order", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })


        viewModel.updateOrderResponse.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        // todo get order by id must be done
                        //order.transactionModel.orderModel.id?.let { viewModel.getOrderById(it) }
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Something went wrong. Error: " + resource.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Updating orders...")
                        progressDialog.show()
                    }

                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(this, "No internet Connection", Toast.LENGTH_LONG).show()
                    }

                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Empty Order", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_close -> {
                onBackPressed()
            }
        }
    }

    private fun showSecretKeyBottomSheet(orderItemListModel: OrderItemListModel) {
        val dialogBinding: BottomSheetSecretKeyBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_secret_key, null, false)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        dialogBinding.buttonConfirm.setOnClickListener {
            if (dialogBinding.editSecretKey.text.toString()
                    .isNotEmpty() && dialogBinding.editSecretKey.text.toString().length == 6
                && dialogBinding.editSecretKey.text.toString().matches(Regex("\\d+"))
            ) {
                val orderModel = OrderModel(id = orderItemListModel.transactionModel.orderModel.id)

                if (orderItemListModel.transactionModel.orderModel.deliveryLocation == null)
                    orderModel.orderStatus = AppConstants.STATUS.COMPLETED.name
                else
                    orderModel.orderStatus = AppConstants.STATUS.DELIVERED.name

                orderModel.secretKey = dialogBinding.editSecretKey.text.toString()
                viewModel.updateOrder(orderModel)
            }
            dialog.dismiss()
        }
    }
}
