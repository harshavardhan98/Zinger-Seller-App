package com.food.ordering.zinger.seller.ui.profile

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
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
import com.food.ordering.zinger.seller.databinding.BottomSheetAddEditMenuItemBinding
import com.food.ordering.zinger.seller.databinding.BottomSheetVerifyOtpBinding
import com.food.ordering.zinger.seller.di.networkModule
import com.food.ordering.zinger.seller.ui.home.HomeActivity
import com.food.ordering.zinger.seller.ui.otp.OTPActivity
import com.food.ordering.zinger.seller.ui.otp.OTPViewModel
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import java.util.concurrent.TimeUnit

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: ProfileViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var dialogBinding: BottomSheetVerifyOtpBinding
    lateinit var verificationCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId: String
    lateinit var dialog: BottomSheetDialog
    lateinit var countDownTimer: CountDownTimer
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

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

        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
        countDownTimer = object : CountDownTimer(10000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                dialogBinding.textResendOtp.setText("Resend OTP (" + millisUntilFinished / 1000 + ")")
            }

            override fun onFinish() {
                dialogBinding.textResendOtp.setText("Resend OTP")
                dialogBinding.textResendOtp.isEnabled=true
            }
        }

        binding.buttonUpdate.setOnClickListener(View.OnClickListener {


            if (binding.editName.isEnabled)
                Toast.makeText(this, "Please confirm name change", Toast.LENGTH_LONG).show()
            else if (binding.editEmail.isEnabled)
                Toast.makeText(this, "Please confirm email change", Toast.LENGTH_LONG).show()
            else if (binding.editMobile.isEnabled)
                Toast.makeText(this, "Please confirm mobile number change", Toast.LENGTH_LONG).show()
            else if(binding.editMobile.editableText.toString().length!=10 || !binding.editMobile.editableText.toString().matches(Regex("\\d+")))
                Toast.makeText(this, "Incorrect mobile number", Toast.LENGTH_LONG).show()
            else {
                val name = binding.editName.editableText.toString()
                val email = binding.editEmail.editableText.toString()
                val mobile = binding.editMobile.editableText.toString()

                val fcmToken = if(preferencesHelper.fcmToken!=null) preferencesHelper.fcmToken else " "

                if (mobile != preferencesHelper.mobile) {
                    sendOtp(mobile)
                    showOtpVerificationBottomSheet(mobile)
                }
                else if (!name.equals(preferencesHelper.name) ||
                    !email.equals(preferencesHelper.email) ||
                    !mobile.equals(preferencesHelper.mobile)
                ) {
                    // check if all the tick mark is selected
                    viewModel.updateProfile(
                        UserModel(
                            id = preferencesHelper.id, name = name, email = email, mobile = mobile
                            ,notificationToken = arrayListOf(fcmToken!!)
                        )
                    )
                }

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
            if (!binding.editMobile.isEnabled)
                binding.imageEditMobile.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditMobile.setImageResource(R.drawable.ic_edit)
            binding.editMobile.isEnabled = !binding.editMobile.isEnabled
        })

        verificationCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Verification Successful!", Toast.LENGTH_LONG).show()
                dialogBinding.editOtp.setText(p0.smsCode)
                viewModel.signInWithPhoneAuthCredential(p0, this@ProfileActivity)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                progressDialog.dismiss()
                p0.printStackTrace()
                Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
                dialogBinding.editOtp.setText("")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                progressDialog.dismiss()
                storedVerificationId = verificationId
                resendToken = token
                countDownTimer.start()
                dialogBinding.textResendOtp.isEnabled = false
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
            }
        }

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
                        dialog.let { dialog.dismiss() }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.let { dialog.dismiss() }
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        dialog.let { dialog.dismiss() }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Updating Profile...")
                        progressDialog.show()
                    }
                }
            }
        })

        viewModel.verifyOtpStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Verifying OTP...")
                        progressDialog.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                       // dialog.dismiss()
                        val name = binding.editName.editableText.toString()
                        val email = binding.editEmail.editableText.toString()
                        val mobile = binding.editMobile.editableText.toString()
                        val fcmToken = ArrayList<String>()

                        preferencesHelper.fcmToken?.let { fcmToken.add(it) }
                        
                        viewModel.updateProfile(
                            UserModel(
                                id = preferencesHelper.id,
                                name = name,
                                email = email,
                                mobile = mobile,
                                notificationToken = fcmToken
                            )
                        )
                        countDownTimer.cancel()
                    }

                    Resource.Status.ERROR -> {
                        //dialog.dismiss()
                        Toast.makeText(this, "OTP verification failed ", Toast.LENGTH_LONG).show()
                        countDownTimer.cancel()
                    }

                }
            }

        })
    }

    private fun showOtpVerificationBottomSheet(number: String) {

        dialogBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.bottom_sheet_verify_otp,
                null,
                false
            )

        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.textResendOtp.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "OTP resent", Toast.LENGTH_LONG).show()
            resendVerificationCode(binding.editMobile.text.toString(), resendToken)
        })

        dialogBinding.buttonVerify.setOnClickListener {
            if (dialogBinding.editOtp.text?.length?.compareTo(6) == 0) {
                val credential = PhoneAuthProvider.getCredential(
                    storedVerificationId,
                    dialogBinding.editOtp.text.toString()
                )
                viewModel.signInWithPhoneAuthCredential(credential, context = this)
            } else {
                Toast.makeText(this, "Wrong OTP length", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendOtp(number: String) {
        progressDialog.setMessage("Sending OTP")
        progressDialog.show()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91"+number, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            verificationCallBack
        )
    }

    fun resendVerificationCode(number: String, token: PhoneAuthProvider.ForceResendingToken) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91"+number,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            verificationCallBack,         // OnVerificationStateChangedCallbacks
            token // ForceResendingToken from callbacks
        );

    }
}
