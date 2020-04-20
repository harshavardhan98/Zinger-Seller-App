package com.food.ordering.zinger.seller.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.databinding.ActivityLoginBinding
import com.food.ordering.zinger.seller.databinding.ActivityProfileBinding
import com.food.ordering.zinger.seller.di.networkModule
import com.food.ordering.zinger.seller.ui.home.HomeActivity
import com.food.ordering.zinger.seller.ui.otp.OTPActivity
import com.food.ordering.zinger.seller.ui.otp.OTPViewModel
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: ProfileViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initView()
        setListener()
        setObserver()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)

        binding.editName.setText(preferencesHelper.name)
        binding.editEmail.setText(preferencesHelper.email)
        binding.editMobile.setText(preferencesHelper.mobile)
    }

    private fun setListener() {
        binding.buttonUpdate.setOnClickListener(View.OnClickListener {
            var name = binding.editName.editableText.toString()
            var email = binding.editEmail.editableText.toString()
            var mobile = binding.editMobile.editableText.toString()

            if (!name.equals(preferencesHelper.name) ||
                !email.equals(preferencesHelper.email) ||
                !mobile.equals(preferencesHelper.mobile)
            ) {
                viewModel.updateProfile(
                    UserModel(
                        id = preferencesHelper.id, name = name, email = email, mobile = mobile
                    )
                )
            }
        })

        binding.imageEditName.setOnClickListener(View.OnClickListener {
            if (!binding.editName.isEnabled)
                binding.imageEditName.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditName.setImageResource(R.drawable.ic_edit)
            binding.editName.isEnabled = !(binding.editName.isEnabled)
        })

        binding.imageEditEmail.setOnClickListener(View.OnClickListener {
            if (!binding.editEmail.isEnabled)
                binding.imageEditEmail.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditEmail.setImageResource(R.drawable.ic_edit)
            binding.editEmail.isEnabled = !binding.editEmail.isEnabled
        })

        binding.imageEditMobile.setOnClickListener(View.OnClickListener {
            //TODO: Verify OTP when changing Mobile Number
            if (!binding.editMobile.isEnabled)
                binding.imageEditMobile.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditMobile.setImageResource(R.drawable.ic_edit)
            binding.editMobile.isEnabled = !binding.editMobile.isEnabled
        })
    }

    private fun setObserver() {
        viewModel.performUpdateProfileStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        preferencesHelper.name = binding.editName.editableText.toString()
                        preferencesHelper.email = binding.editEmail.editableText.toString()
                        preferencesHelper.mobile = binding.editMobile.editableText.toString()
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Profile Successfully Updated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Updating...")
                        progressDialog.show()
                    }
                }
            }
        })
    }
}
