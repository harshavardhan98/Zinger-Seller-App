package com.food.ordering.zinger.seller.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
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
import com.food.ordering.zinger.seller.databinding.FragmentNewOrdersBinding
import com.food.ordering.zinger.seller.ui.order.OrderViewModel
import com.food.ordering.zinger.seller.ui.orderDetail.OrderDetailActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel


class  NewOrdersFragment : Fragment() {

    lateinit var binding: FragmentNewOrdersBinding
    private val viewModel: OrderViewModel by sharedViewModel()
    private lateinit var progressDialog: ProgressDialog
    private val preferencesHelper: PreferencesHelper by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_orders, container, false)
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
        //viewModel.getOrderByShopId(1)
    }

    private fun setObservers() {


        viewModel.orderByShopIdResponse.observe(viewLifecycleOwner, Observer { resource ->
            if(resource!=null){
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        ordersList.clear()
                        if(!resource.data?.data.isNullOrEmpty()){
                            resource.data?.data?.let { it1 ->
                                ordersList.addAll(it1.filter { it.transactionModel.orderModel.orderStatus.equals(AppConstants.STATUS.PLACED.name) })
                            }
                            orderAdapter.notifyDataSetChanged()
                        }
                    }

                    Resource.Status.ERROR ->{
                        progressDialog.dismiss()
                        Toast.makeText(context,"Something went wrong. Error:\n"+resource.message, Toast.LENGTH_LONG).show()
                    }

                    Resource.Status.LOADING ->{
                        progressDialog.setMessage("Fetching Orders...")
                        progressDialog.show()
                    }

                    Resource.Status.OFFLINE_ERROR ->{
                        progressDialog.dismiss()
                        Toast.makeText(context,"Offline error", Toast.LENGTH_LONG).show()
                    }

                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        Toast.makeText(context,"Empty items", Toast.LENGTH_LONG).show()
                    }
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
                val orderModel = OrderModel(id = orderItemListModel!!.transactionModel.orderModel.id,orderStatus = AppConstants.STATUS.ACCEPTED.name)
                viewModel.updateOrder(orderModel)
            }

            override fun onCancelClick(orderItemListModel: OrderItemListModel?, position: Int) {
                val orderModel = OrderModel(id = orderItemListModel!!.transactionModel.orderModel.id,orderStatus = AppConstants.STATUS.CANCELLED_BY_SELLER.name )
                viewModel.updateOrder(orderModel)
            }
        })
        binding.recyclerOrders.layoutManager = LinearLayoutManager(context!!,LinearLayoutManager.VERTICAL,false)
        binding.recyclerOrders.adapter = orderAdapter
        orderAdapter.notifyDataSetChanged()
    }

}
