package com.food.ordering.zinger.seller.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.seller.MainActivity
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.databinding.ActivityLoginBinding
import com.food.ordering.zinger.seller.ui.otp.OTPActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val preferencesHelper: PreferencesHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
        setListener()
        if (!preferencesHelper.oauthId.isNullOrEmpty() && preferencesHelper.id!=null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
    }

    private fun setListener() {
        binding.buttonLogin.setOnClickListener {
            if (binding.editPhone.text.toString().isNotEmpty()) {
                //TODO Phone number validation
                val intent = Intent(applicationContext, OTPActivity::class.java)
                intent.putExtra(AppConstants.PREFS_SELLER_MOBILE, "+91"+binding.editPhone.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Phone number is blank!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
