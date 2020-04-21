package com.food.ordering.zinger.seller.ui.home

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
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.OrderItemList
import com.food.ordering.zinger.seller.databinding.FragmentReadyBinding
import com.food.ordering.zinger.seller.ui.order.OrderViewModel
import com.food.ordering.zinger.seller.utils.AppConstants
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class ReadyFragment : Fragment() {

    lateinit var binding:FragmentReadyBinding
    private val viewModel: OrderViewModel by sharedViewModel()

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
        //TODO swipe refresh layout on refresh
        //viewModel.getOrderByShopId(1)
    }

    private fun setObservers() {

        viewModel.orderByShopIdResponse.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    ordersList.clear()
                    if(!it.data?.data.isNullOrEmpty()){
                        it.data?.data?.let { it1 ->
                            //ordersList.addAll(it1)
                            ordersList.addAll(it1.filter { it.transactionModel.orderModel.orderStatus.equals(
                                AppConstants.STATUS.READY.name) })
                        }
                        orderAdapter.notifyDataSetChanged()
                    }

                }
                // todo handle all other cases for view model
            }
        })

    }

    var ordersList: ArrayList<OrderItemList> = ArrayList()
    lateinit var orderAdapter: OrdersAdapter
    private fun updateUI() {
        println("Order list size "+ordersList.size)
        orderAdapter = OrdersAdapter(ordersList, object: OrdersAdapter.OnItemClickListener{
            override fun onItemClick(item: OrderItemList?, position: Int) {
                //TODO navigate to detail
            }

            override fun onUpdateClick(orderItemListModel: OrderItemList?, position: Int) {
                //TODO show order status update bottom sheet
                Toast.makeText(activity,"testing ready", Toast.LENGTH_LONG).show()

                var orderModel = orderItemListModel!!.transactionModel.orderModel
                // set the secret key

                if(orderItemListModel!!.transactionModel.orderModel.deliveryPrice==null)
                    orderModel.orderStatus=AppConstants.STATUS.COMPLETED.name
                else
                    orderModel.orderStatus=AppConstants.STATUS.DELIVERED.name

                viewModel.updateOrder(orderModel)

            }

        })
        binding.recyclerOrders.layoutManager = LinearLayoutManager(context!!,
            LinearLayoutManager.VERTICAL,false)
        binding.recyclerOrders.adapter = orderAdapter
        orderAdapter.notifyDataSetChanged()
    }


}
