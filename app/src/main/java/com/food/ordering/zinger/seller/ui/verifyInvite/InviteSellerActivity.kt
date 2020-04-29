package com.food.ordering.zinger.seller.ui.verifyInvite

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.databinding.ActivityInviteSellerBinding
import com.food.ordering.zinger.seller.databinding.ActivitySellerBinding
import com.food.ordering.zinger.seller.ui.otp.OTPActivity
import com.food.ordering.zinger.seller.ui.seller.SellerViewModel
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class InviteSellerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInviteSellerBinding
    private val viewModel: InviteSellerViewModel by viewModel()
    private lateinit var errorSnackBar: Snackbar
    var phone = "0"
    var shopId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_seller)

        initView()
        setObservers()

        val data = intent?.data?.toString()?.split("/")
        phone = data?.get(data.size-1).toString()
        shopId = data?.get(data.size-2).toString()

        if(phone?.length!=10 || !phone.matches(Regex("\\d+")) || !shopId?.matches(Regex("\\d+"))!!){
            Toast.makeText(this,"Invalid link",Toast.LENGTH_LONG).show()
        }
        else{
            viewModel.verifyInvite(shopId.toInt(),phone)
        }
    }


    private fun initView(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invite_seller)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)

    }


    private fun setObservers(){
        viewModel.verifyInviteResponse.observe(this, Observer { resource ->
            if(resource!=null){
                when(resource.status){
                    Resource.Status.SUCCESS -> {
                        Toast.makeText(applicationContext, "Success ", Toast.LENGTH_SHORT).show()
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()

                        val intent = Intent(applicationContext, OTPActivity::class.java)
                        intent.putExtra(AppConstants.PREFS_SELLER_MOBILE, "+91"+phone)
                        intent.putExtra(AppConstants.SELLER_INVITE,true)
                        intent.putExtra(AppConstants.SELLER_SHOP,shopId)
                        startActivity(intent)

                    }

                    Resource.Status.ERROR -> {
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                        errorSnackBar.setText("Error: "+resource.message.toString())
                        errorSnackBar.show()
                    }

                    Resource.Status.OFFLINE_ERROR -> {
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                    }

                    Resource.Status.LOADING -> {
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("loading_animation.json")
                        binding.animationView.playAnimation()
                    }
                }
            }
        })
    }
}
