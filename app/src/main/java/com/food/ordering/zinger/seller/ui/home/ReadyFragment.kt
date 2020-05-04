package com.food.ordering.zinger.seller.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.OrderItemListModel
import com.food.ordering.zinger.seller.data.model.OrderModel
import com.food.ordering.zinger.seller.databinding.BottomSheetSecretKeyBinding
import com.food.ordering.zinger.seller.databinding.FragmentReadyBinding
import com.food.ordering.zinger.seller.ui.orderdetail.OrderDetailActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class ReadyFragment : Fragment() {

    lateinit var binding: FragmentReadyBinding
    private val viewModel: OrderViewModel by sharedViewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var errorSnackBar: Snackbar
    private lateinit var updateErrorSnackbar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ready, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObservers()
    }

    private fun initView() {
        updateUI()
        progressDialog = ProgressDialog(activity)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        updateErrorSnackbar = Snackbar.make(binding.root, " ", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(context!!, R.color.accent))
        errorSnackBar.setAction("Try Again") {
            viewModel.getOrderByShopId(preferencesHelper.currentShop)
        }
        errorSnackBar.dismiss()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getOrderByShopId(preferencesHelper.currentShop)
        }

    }

    private fun setObservers() {

        viewModel.orderByShopIdResponse.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    ordersList.clear()
                    if (!it.data.isNullOrEmpty()) {
                        it.data.let { it1 ->
                            //ordersList.addAll(it1)
                            preferencesHelper.role?.let { role ->
                                if (role == AppConstants.ROLE.DELIVERY.name) {
                                    ordersList.addAll(it1.filter { order ->
                                        order.orderStatusModel.last().orderStatus == AppConstants.STATUS.OUT_FOR_DELIVERY.name
                                    })
                                } else {
                                    ordersList.addAll(it1.filter { order ->
                                        order.orderStatusModel.last().orderStatus == AppConstants.STATUS.READY.name || order.orderStatusModel.last().orderStatus == AppConstants.STATUS.OUT_FOR_DELIVERY.name
                                    })
                                }
                                ordersList.forEach { order ->
                                    order.transactionModel.orderModel.orderStatus =
                                        order.orderStatusModel.last().orderStatus
                                }
                            }
                        }
                    }
                    orderAdapter.notifyDataSetChanged()
                    if (ordersList.isEmpty()) {
                        showEmptyStateAnimation()
                    } else {
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                        errorSnackBar.dismiss()
                    }
                }
                Resource.Status.ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("order_failed_animation.json")
                    binding.animationView.playAnimation()
                    ordersList.clear()
                    orderAdapter.notifyDataSetChanged()
                    errorSnackBar.setText("Error: " + it.message)

                    //Toast.makeText(context,"Something went wrong. Error:\n"+it.message, Toast.LENGTH_LONG).show()

                }

                Resource.Status.LOADING -> {
                    ordersList.clear()
                    orderAdapter.notifyDataSetChanged()
                    if (!binding.swipeRefreshLayout.isRefreshing) {
                        binding.layoutStates.visibility = View.VISIBLE
                        binding.animationView.visibility = View.GONE
                    }
                    errorSnackBar.dismiss()
                }

                Resource.Status.OFFLINE_ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("no_internet_connection_animation.json")
                    binding.animationView.playAnimation()
                    errorSnackBar.setText("No Internet Connection")
                    ordersList.clear()
                    orderAdapter.notifyDataSetChanged()
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }

                Resource.Status.EMPTY -> {
                    ordersList.clear()
                    orderAdapter.notifyDataSetChanged()
                    showEmptyStateAnimation()
                }

            }
        })

        homeViewModel.updateOrderResponse.observe(viewLifecycleOwner, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        //viewModel.getOrderByShopId(preferencesHelper.currentShop)
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        resource.message?.let {
                            if (it.toLowerCase().contains("secret")) {
                                updateErrorSnackbar.setText(resource.message)
                                updateErrorSnackbar.show()
                            }
                        }
                    }

                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Updating orders...")
                        progressDialog.show()
                    }

                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Offline error", Toast.LENGTH_LONG).show()
                    }

                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        Toast.makeText(context, "No orders", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })

    }

    private var ordersList: ArrayList<OrderItemListModel> = ArrayList()
    private lateinit var orderAdapter: OrdersAdapter
    private fun updateUI() {
        println("Order list size " + ordersList.size)
        orderAdapter = OrdersAdapter(ordersList, object : OrdersAdapter.OnItemClickListener {
            override fun onItemClick(orderItemListModel: OrderItemListModel?, position: Int) {
                val intent = Intent(context, OrderDetailActivity::class.java)
                intent.putExtra(AppConstants.ORDER_DETAIL, Gson().toJson(orderItemListModel))
                startActivity(intent)
            }

            override fun onUpdateClick(orderItemListModel: OrderItemListModel?, position: Int) {
                orderItemListModel?.let { showSecretKeyBottomSheet(it) }
            }

            override fun onCancelClick(orderItemListModel: OrderItemListModel?, position: Int) {
            }
        })
        binding.recyclerOrders.layoutManager = LinearLayoutManager(
            context!!,
            LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerOrders.adapter = orderAdapter
        orderAdapter.notifyDataSetChanged()
    }

    private fun showSecretKeyBottomSheet(orderItemListModel: OrderItemListModel) {
        val dialogBinding: BottomSheetSecretKeyBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_secret_key, null, false)
        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.editSecretKey.setOnEditorActionListener { _, actionId, _ ->
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

    private fun updateOrderRequest(
        dialog: BottomSheetDialog, dialogBinding: BottomSheetSecretKeyBinding
        , orderItemListModel: OrderItemListModel
    ) {

        if (dialogBinding.editSecretKey.text.toString()
                .isNotEmpty() && dialogBinding.editSecretKey.text.toString().length == 6
            && dialogBinding.editSecretKey.text.toString().matches(Regex("\\d+"))
        ) {
            val orderModel =
                OrderModel(id = orderItemListModel.transactionModel.orderModel.id)

            if (orderItemListModel.transactionModel.orderModel.deliveryLocation == null)
                orderModel.orderStatus = AppConstants.STATUS.COMPLETED.name
            else
                orderModel.orderStatus = AppConstants.STATUS.DELIVERED.name

            orderModel.secretKey = dialogBinding.editSecretKey.text.toString()
            homeViewModel.updateOrder(orderModel)
        }
        dialog.dismiss()
    }


    private fun showEmptyStateAnimation() {
        binding.swipeRefreshLayout.isRefreshing = false
        binding.layoutStates.visibility = View.GONE
        binding.animationView.visibility = View.VISIBLE
        binding.animationView.loop(true)
        binding.animationView.setAnimation("empty_animation.json")
        binding.animationView.playAnimation()
        errorSnackBar.setText("No Orders is ready for pickup or delivery")
        Handler().postDelayed({ errorSnackBar.show() }, 500)
    }

    override fun onPause() {
        super.onPause()
        errorSnackBar.dismiss()
        updateErrorSnackbar.dismiss()
    }

}
