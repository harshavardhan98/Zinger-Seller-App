package com.food.ordering.zinger.seller.ui.webview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.databinding.ActivityWebViewBinding
import com.food.ordering.zinger.seller.utils.AppConstants
import org.koin.android.ext.android.inject

class WebViewActivity : AppCompatActivity() {


    private lateinit var binding: ActivityWebViewBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private var url:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListener()
    }

    private fun getArgs(){
        url = intent.getStringExtra(AppConstants.NOTIFICATIONTYPE.URL.name)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        binding.webView.getSettings().setJavaScriptEnabled(true)
        url?.let{
            binding.webView.loadUrl(it)
        }?.run {
            binding.webView.loadUrl("https://shrikanthravi.me")
        }

    }

    private fun setListener() {
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
    }

}
