package com.food.ordering.zinger.seller.ui.orderdetail

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.*
import com.food.ordering.zinger.seller.databinding.ActivityOrderDetailBinding
import com.food.ordering.zinger.seller.databinding.BottomSheetSecretKeyBinding
import com.food.ordering.zinger.seller.utils.AppConstants
import com.food.ordering.zinger.seller.utils.StatusHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class OrderDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOrderDetailBinding
    private val viewModel: OrderDetailViewModel by viewModel()
    private lateinit var orderAdapter: OrderItemAdapter
    private lateinit var orderTimelineAdapter: OrderTimelineAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderItems> = ArrayList()
    private lateinit var order: OrderItemListModel
    var isPickup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getArgs()
        setListeners()
        setObservers()
    }

    private fun getArgs() {
        if (intent.hasExtra(AppConstants.ORDER_DETAIL)) {
            order = Gson().fromJson(
                intent.getStringExtra(AppConstants.ORDER_DETAIL),
                OrderItemListModel::class.java
            )
            initView()
        } else if (intent.hasExtra(AppConstants.INTENT_ORDER_ID)) {

            binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
            binding.imageClose.setOnClickListener(this)
            progressDialog = ProgressDialog(this)
            progressDialog.setCancelable(false)


            order = OrderItemListModel(
                ArrayList(),
                TransactionModel(orderModel = OrderModel()),
                ArrayList()
            )
            setupShopRecyclerView()

            val orderId = intent.getIntExtra(AppConstants.INTENT_ORDER_ID, -1)

            if (orderId != -1) {
                val orderModelRequest = OrderModel(id = orderId)
                if (intent.hasExtra(AppConstants.INTENT_ACCEPT)) {
                    if (intent.getBooleanExtra(AppConstants.INTENT_ACCEPT, false)) {
                        orderModelRequest.orderStatus = AppConstants.STATUS.ACCEPTED.name
                        viewModel.updateOrder(orderModelRequest)
                    } else {
                        viewModel.getOrderById(orderId)
                    }
                } else if (intent.hasExtra(AppConstants.INTENT_DECLINE)) {
                    if (intent.getBooleanExtra(AppConstants.INTENT_DECLINE, false)) {
                        orderModelRequest.orderStatus = AppConstants.STATUS.CANCELLED_BY_SELLER.name
                        viewModel.updateOrder(orderModelRequest)
                    } else {
                        viewModel.getOrderById(orderId)
                    }
                } else {
                    viewModel.getOrderById(orderId)
                }

            } else {
                Toast.makeText(this, "Invalid Order Id", Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        } else {
            Toast.makeText(this, "OnBack Pressed", Toast.LENGTH_LONG)
            onBackPressed()
        }

    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)


        var status = order.orderStatusModel.last().orderStatus
        order.transactionModel.orderModel.orderStatus = status
        // CANCELLED_BY_SELLER,CANCELLED_BY_USER, DELIVERED, COMPLETED
        var terminalStates = arrayOf<String>(
            AppConstants.STATUS.COMPLETED.name,
            AppConstants.STATUS.DELIVERED.name,
            AppConstants.STATUS.REFUND_INITIATED.name,
            AppConstants.STATUS.REFUND_COMPLETED.name,
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

        when (status) {
            AppConstants.STATUS.PLACED.name -> {
                binding.textUpdateStatus.text = "ACCEPT"
            }
            AppConstants.STATUS.ACCEPTED.name -> {
                if (order.transactionModel.orderModel.deliveryLocation == null)
                    binding.textUpdateStatus.text = AppConstants.STATUS.READY.name
                else
                    binding.textUpdateStatus.text = "OUT FOR DELIVERY"
            }
            AppConstants.STATUS.READY.name -> {
                binding.textCancel.visibility = View.INVISIBLE
                binding.textCancel.isEnabled = false
                binding.textUpdateStatus.text = "COMPLETE"
            }
            AppConstants.STATUS.OUT_FOR_DELIVERY.name -> {
                binding.textCancel.visibility = View.INVISIBLE
                binding.textCancel.isEnabled = false
                binding.textUpdateStatus.text = "DELIVER"
            }
        }

        setupOrderStatusRecyclerView()
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
            isPickup = false
        } else {
            binding.textDeliveryLocation.text = "Pick up from restaurant"
            isPickup = true
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

        if(!order.transactionModel.orderModel.feedBack.isNullOrEmpty()){
            binding.textRatingFeedback.visibility = View.VISIBLE
            order.transactionModel.orderModel.feedBack?.let{
                binding.textRatingFeedback.text = it
            }
        }else{
            binding.textRatingFeedback.visibility = View.GONE
        }

        var status = order.orderStatusModel.last().orderStatus
        when (status) {
            AppConstants.ORDER_STATUS_PENDING -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PENDING),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                if (isPickup) {
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                } else {
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                }
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_CANCELLED_BY_USER -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_USER),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_INITIATED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_INITIATED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderTimelineAdapter.notifyDataSetChanged()
            }

            AppConstants.ORDER_STATUS_PLACED -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                if (isPickup) {
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                } else {
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                }
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_ACCEPTED -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                if (isPickup) {
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                } else {
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                    orderStatusList.add(
                        OrderStatus(
                            isCurrent = false,
                            isDone = false,
                            name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED),
                            orderStatusList = order.orderStatusModel
                        )
                    )
                }
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_READY -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_COMPLETED -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_DELIVERED -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_REFUND_INITIATED -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_USER),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_INITIATED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_REFUND_COMPLETED -> {
                orderStatusList.clear()
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = false,
                        isDone = true,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_INITIATED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderStatusList.add(
                    OrderStatus(
                        isCurrent = true,
                        isDone = false,
                        name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED),
                        orderStatusList = order.orderStatusModel
                    )
                )
                orderTimelineAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun setListeners() {

        binding.swipeRefreshLayout.setOnRefreshListener {
            order.transactionModel.orderModel.id?.let { viewModel.getOrderById(it) }
        }

        binding.imageCall.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + order.transactionModel.orderModel.userModel?.mobile)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        binding.textCancel.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.confirm_order_status_update))
                .setMessage(getString(R.string.cancel_order_request))
                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    val orderModel = OrderModel(
                        id = order.transactionModel.orderModel.id,
                        orderStatus = AppConstants.STATUS.CANCELLED_BY_SELLER.name
                    )
                    viewModel.updateOrder(orderModel)
                }
                .setNegativeButton(getString(R.string.no)) { dialog, which -> dialog.dismiss() }
                .show()

        }

        binding.textUpdateStatus.setOnClickListener {
            when (order.transactionModel.orderModel.orderStatus) {

                AppConstants.STATUS.PLACED.name -> {

                    MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.confirm_order_status_update))
                        .setMessage(getString(R.string.accept_order_request))
                        .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                            val orderModel = OrderModel(
                                id = order.transactionModel.orderModel.id,
                                orderStatus = AppConstants.STATUS.ACCEPTED.name
                            )
                            viewModel.updateOrder(orderModel)

                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, which -> dialog.dismiss() }
                        .show()


                }

                AppConstants.STATUS.ACCEPTED.name -> {

                    var orderModel = OrderModel(id = order.transactionModel.orderModel.id)
                    var msg = ""
                    if (order.transactionModel.orderModel.deliveryLocation == null) {
                        orderModel.orderStatus = AppConstants.STATUS.READY.name
                        msg = getString(R.string.pickup_order_request)
                    } else {
                        orderModel.orderStatus = AppConstants.STATUS.OUT_FOR_DELIVERY.name
                        msg = getString(R.string.delivery_order_request)
                    }


                    MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.confirm_order_status_update))
                        .setMessage(msg)
                        .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                            viewModel.updateOrder(orderModel)
                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, which -> dialog.dismiss() }
                        .show()


                }

                AppConstants.STATUS.READY.name,
                AppConstants.STATUS.OUT_FOR_DELIVERY.name -> {
                    showSecretKeyBottomSheet(order)
                }
            }

        }

    }

    var orderStatusList: ArrayList<OrderStatus> = ArrayList()
    private fun setupOrderStatusRecyclerView() {
        orderTimelineAdapter = OrderTimelineAdapter(
            applicationContext,
            orderStatusList,
            object : OrderTimelineAdapter.OnItemClickListener {
                override fun onItemClick(item: OrderStatus?, position: Int) {}
            })
        binding.recyclerStatus.layoutManager =
            LinearLayoutManager(this@OrderDetailActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerStatus.adapter = AlphaInAnimationAdapter(orderTimelineAdapter)
    }

    private fun setupShopRecyclerView() {

        orderList.addAll(order.orderItemsList)
        orderAdapter = OrderItemAdapter(
            applicationContext,
            orderList,
            object : OrderItemAdapter.OnItemClickListener {
                override fun onItemClick(item: OrderItems?, position: Int) {
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
                        var intent = Intent(applicationContext, OrderDetailActivity::class.java)
                        intent.putExtra(
                            AppConstants.ORDER_DETAIL,
                            Gson().toJson(resource.data?.data)
                        )
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
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
                        progressDialog.setMessage("Fetching data...")
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

                        order.transactionModel.orderModel.id?.let { viewModel.getOrderById(it) }
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

        dialogBinding.editSecretKey.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    updateOrderRequest(dialog, dialogBinding, orderItemListModel)
                    true
                }
                else -> false
            }
        }

        dialogBinding.buttonConfirm.setOnClickListener {
            updateOrderRequest(dialog, dialogBinding, orderItemListModel)
        }
    }

    fun updateOrderRequest(
        dialog: BottomSheetDialog, dialogBinding: BottomSheetSecretKeyBinding
        , orderItemListModel: OrderItemListModel
    ) {
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
