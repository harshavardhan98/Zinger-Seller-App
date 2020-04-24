package com.food.ordering.zinger.seller.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.food.ordering.zinger.seller.ui.order.OrderViewModel
import com.food.ordering.zinger.seller.ui.orderDetail.OrderDetailActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class ReadyFragment : Fragment() {

    lateinit var binding:FragmentReadyBinding
    private val viewModel: OrderViewModel by sharedViewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var errorSnackBar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
        errorSnackBar.setAction("Try Again"){
            viewModel.getOrderByShopId(preferencesHelper.currentShop)
        }
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
                    if(!it.data?.data.isNullOrEmpty()){
                        it.data?.data?.let { it1 ->
                            //ordersList.addAll(it1)
                            ordersList.addAll(it1.filter { it.transactionModel.orderModel.orderStatus.equals(
                                AppConstants.STATUS.READY.name)||it.transactionModel.orderModel.orderStatus.equals(
                                AppConstants.STATUS.OUT_FOR_DELIVERY.name) })
                        }
                        orderAdapter.notifyDataSetChanged()
                    }
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.GONE
                    binding.animationView.cancelAnimation()
                    errorSnackBar.dismiss()

                }
                Resource.Status.ERROR ->{
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("order_failed_animation.json")
                    binding.animationView.playAnimation()
                    errorSnackBar.setText("Error: "+it.message)

                    //Toast.makeText(context,"Something went wrong. Error:\n"+it.message, Toast.LENGTH_LONG).show()

                }

                Resource.Status.LOADING ->{
                    ordersList.clear()
                    orderAdapter.notifyDataSetChanged()
                    if (!binding.swipeRefreshLayout.isRefreshing) {
                        binding.layoutStates.visibility = View.VISIBLE
                        binding.animationView.visibility = View.GONE
                    }
                    errorSnackBar.dismiss()
                }

                Resource.Status.OFFLINE_ERROR ->{
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("no_internet_connection_animation.json")
                    binding.animationView.playAnimation()
                    errorSnackBar.setText("No Internet Connection")
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }

                Resource.Status.EMPTY -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("empty_animation.json")
                    binding.animationView.playAnimation()
                    errorSnackBar.setText("No Orders available")
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }


            }
        })

        viewModel.updateOrderResponse.observe(viewLifecycleOwner,Observer{resource ->
            if(resource!=null){
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        viewModel.getOrderByShopId(preferencesHelper.currentShop)
                    }

                    Resource.Status.ERROR ->{
                        progressDialog.dismiss()
                        Toast.makeText(context,"Something went wrong. Error:\n"+resource.message, Toast.LENGTH_LONG).show()
                    }

                    Resource.Status.LOADING ->{
                        progressDialog.setMessage("Updating orders...")
                        progressDialog.show()
                    }

                    Resource.Status.OFFLINE_ERROR ->{
                        progressDialog.dismiss()
                        Toast.makeText(context,"Offline error", Toast.LENGTH_LONG).show()
                    }

                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        Toast.makeText(context,"No orders", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })

    }

    var ordersList: ArrayList<OrderItemListModel> = ArrayList()
    lateinit var orderAdapter: OrdersAdapter
    private fun updateUI() {
        println("Order list size "+ordersList.size)
        orderAdapter = OrdersAdapter(ordersList, object: OrdersAdapter.OnItemClickListener{
            override fun onItemClick(item: OrderItemListModel?, position: Int) {
                val intent = Intent(context, OrderDetailActivity::class.java)
                intent.putExtra(AppConstants.ORDER_DETAIL, Gson().toJson(item))
                startActivity(intent)
            }

            override fun onUpdateClick(orderItemListModel: OrderItemListModel?, position: Int) {
                orderItemListModel?.let { showSecretKeyBottomSheet(it) }
            }

            override fun onCancelClick(orderItemListModel: OrderItemListModel?, position: Int) {
            }
        })
        binding.recyclerOrders.layoutManager = LinearLayoutManager(context!!,
            LinearLayoutManager.VERTICAL,false)
        binding.recyclerOrders.adapter = orderAdapter
        orderAdapter.notifyDataSetChanged()
    }

    private fun showSecretKeyBottomSheet(orderItemListModel: OrderItemListModel){
        val dialogBinding: BottomSheetSecretKeyBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_secret_key, null, false)
        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        dialogBinding.buttonConfirm.setOnClickListener {
            if(dialogBinding.editSecretKey.text.toString().isNotEmpty() && dialogBinding.editSecretKey.text.toString().length==6
                && dialogBinding.editSecretKey.text.toString().matches(Regex("\\d+"))){
                val orderModel = OrderModel(id=orderItemListModel!!.transactionModel.orderModel.id)

                if(orderItemListModel!!.transactionModel.orderModel.deliveryLocation==null)
                    orderModel.orderStatus=AppConstants.STATUS.COMPLETED.name
                else
                    orderModel.orderStatus=AppConstants.STATUS.DELIVERED.name

                orderModel.secretKey = dialogBinding.editSecretKey.text.toString()
                viewModel.updateOrder(orderModel)
            }
            dialog.dismiss()
        }
    }


}
