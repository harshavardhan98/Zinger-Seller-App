package com.food.ordering.zinger.seller.ui.display

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.databinding.ActivityDisplayBinding
import com.food.ordering.zinger.seller.utils.AppConstants
import com.squareup.picasso.Picasso

class DisplayActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDisplayBinding
    private lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
    }

    private fun getArgs(){
       imageUrl = intent.getStringExtra(AppConstants.DISPLAY_IMAGE_DETAIL)
    }

    private fun initView(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_display)

        Picasso.get().load(imageUrl)
            .placeholder(R.drawable.ic_shop)
            .into(binding.imageDisplayImage)
    }
}
