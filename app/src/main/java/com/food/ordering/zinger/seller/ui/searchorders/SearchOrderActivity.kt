package com.food.ordering.zinger.seller.ui.searchorders

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.OrderItemListModel
import com.food.ordering.zinger.seller.databinding.ActivitySearchOrderBinding
import com.food.ordering.zinger.seller.ui.orderdetail.OrderDetailActivity
import com.food.ordering.zinger.seller.ui.orderhistory.OrderHistoryAdapter
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

class SearchOrderActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySearchOrderBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: SearchOrderViewModel by viewModel()
    private lateinit var orderAdapter: OrderHistoryAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderItemListModel> = ArrayList()
    private var timer: Timer? = null
    private lateinit var errorSnackBar: Snackbar
    var pageCnt = 5
    var searchTerm = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_order)

        initView()
        setListener()
        setObservers()
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
        val text = "<font color=#000000>Search past</font> <font color=#FF4141>orders</font>"
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

    private fun setListener() {
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (s.toString().length > 2) {
                            //viewModel.getMenu(preferencesHelper.getPlace()?.id.toString(), s.toString(), shopId, isGlobalSearch)
                            if (searchTerm != s.toString()) {
                                runOnUiThread {
                                    isLoading = false
                                    isLastPage = false
                                    page = 1
                                    searchTerm = s.toString()
                                    getOrders(s.toString())
                                }
                            }
                        } else {
                            runOnUiThread {
                                orderList.clear()
                                orderAdapter.notifyDataSetChanged()
                                binding.appBarLayout.setExpanded(true, true)
                            }
                        }
                    }
                }, 600)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                timer?.cancel()
            }
        })
    }


    var isFirstTime = true
    private fun setObservers() {
        viewModel.searchOrderResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.LOADING -> {
                        isLoading = true
                        if (isFirstTime) {
                            binding.layoutStates.visibility = View.VISIBLE
                            binding.animationView.visibility = View.GONE
                        } else {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        errorSnackBar.dismiss()
                    }
                    Resource.Status.EMPTY -> {
                        isLoading = false
                        isLastPage = true
                        binding.progressBar.visibility = View.GONE
                        if (isFirstTime) {
                            binding.layoutStates.visibility = View.GONE
                            binding.animationView.visibility = View.VISIBLE
                            binding.animationView.loop(true)
                            binding.animationView.setAnimation("empty_animation.json")
                            binding.animationView.playAnimation()
                            orderList.clear()
                            orderAdapter.notifyDataSetChanged()
                            errorSnackBar.setText("No orders found")
                            Handler().postDelayed({ errorSnackBar.show() }, 500)
                        }
                    }
                    Resource.Status.SUCCESS -> {
                        isLoading = false
                        binding.progressBar.visibility = View.GONE
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                        errorSnackBar.dismiss()
                        if (isFirstTime) {
                            orderList.clear()
                        }
                        println("size testing " + resource.data?.data?.size)
                        resource.data?.data?.let { it1 -> orderList.addAll(it1) }
                        if (resource.data?.data.isNullOrEmpty()) {
                            isLastPage = true
                        } else {
                            if (resource.data?.data?.size!! < pageCnt)
                                isLastPage = true

                            if (!isLastPage)
                                page += 1
                        }
                        orderAdapter.notifyDataSetChanged()
                        isFirstTime = false
                        //binding.appBarLayout.setExpanded(false, true)
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        isLoading = false
                        binding.progressBar.visibility = View.GONE
                        if (isFirstTime) {
                            binding.layoutStates.visibility = View.GONE
                            binding.animationView.visibility = View.VISIBLE
                            binding.animationView.loop(true)
                            binding.animationView.setAnimation("no_internet_connection_animation.json")
                            binding.animationView.playAnimation()
                            errorSnackBar.setText("No Internet Connection")
                            Handler().postDelayed({ errorSnackBar.show() }, 500)
                        }
                        //binding.appBarLayout.setExpanded(true, true)

                    }
                    Resource.Status.ERROR -> {
                        isLoading = false
                        binding.progressBar.visibility = View.GONE
                        if (isFirstTime) {
                            binding.layoutStates.visibility = View.GONE
                            binding.animationView.visibility = View.VISIBLE
                            binding.animationView.loop(true)
                            binding.animationView.setAnimation("order_failed_animation.json")
                            binding.animationView.playAnimation()
                            errorSnackBar.setText("Something went wrong")
                            Handler().postDelayed({ errorSnackBar.show() }, 500)
                        }
                        //binding.appBarLayout.setExpanded(true, true)
                    }
                }
            }
        })

    }

    var isLoading = false
    var isLastPage = false
    var page = 1
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
        if(searchTerm.length>0){
            orderList.clear()
            orderAdapter.notifyDataSetChanged()
            preferencesHelper.currentShop?.let {
                viewModel.getOrderBySearchTerm(it, searchTerm, 1, pageCnt)
            }
        }
    }
}
