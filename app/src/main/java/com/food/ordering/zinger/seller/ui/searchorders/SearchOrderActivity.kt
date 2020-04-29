package com.food.ordering.zinger.seller.ui.searchorders

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.OrderItemListModel
import com.food.ordering.zinger.seller.databinding.ActivityOrderHistoryBinding
import com.food.ordering.zinger.seller.ui.order.OrderViewModel
import com.food.ordering.zinger.seller.ui.orderdetail.OrderDetailActivity
import com.food.ordering.zinger.seller.ui.orderhistory.OrderHistoryAdapter
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SearchOrderActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOrderHistoryBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: SearchOrderViewModel by viewModel()
    private lateinit var orderAdapter: OrderHistoryAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderItemListModel> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    var pageCnt = 5


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_order)

        initView()

    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_order)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        val text = "<font color=#000000>Manage and track</font> <font color=#FF4141>orders</font>"
        binding.titleOrders.text = Html.fromHtml(text)
        setupShopRecyclerView()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_close -> {
                onBackPressed()
            }
        }
    }

    var isLoading = false
    var isLastPage = false
    var page = 1
    var searchTerm = ""
    private fun setupShopRecyclerView() {
        orderAdapter =
            OrderHistoryAdapter(orderList, object : OrderHistoryAdapter.OnItemClickListener {
                override fun onItemClick(item: OrderItemListModel?, position: Int) {
                    val intent = Intent(applicationContext, OrderDetailActivity::class.java)
                    intent.putExtra(AppConstants.ORDER_DETAIL, Gson().toJson(item))
                    startActivity(intent)
                }
            })
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.layoutManager = layoutManager
        binding.recyclerShops.adapter = AlphaInAnimationAdapter(orderAdapter)
        binding.recyclerShops.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= pageCnt) {
                        preferencesHelper.currentShop?.let {
                            viewModel.getOrderBySearchTerm(it, searchTerm, page, pageCnt)
                        }
                    }
                }
            }
        })
    }

    private fun getOrders(searchTerm: String) {
        orderList.clear()
        orderAdapter.notifyDataSetChanged()
        preferencesHelper.currentShop?.let {
            viewModel.getOrderBySearchTerm(it, searchTerm, 1, pageCnt)
        }
    }
}
