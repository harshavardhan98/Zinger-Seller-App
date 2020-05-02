package com.food.ordering.zinger.seller.ui.otp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.ShopModel
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.data.model.UserShopModel
import com.food.ordering.zinger.seller.databinding.ActivityOTPBinding
import com.food.ordering.zinger.seller.di.networkModule
import com.food.ordering.zinger.seller.ui.home.HomeActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import java.util.concurrent.TimeUnit


class OTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOTPBinding
    private val viewModel: OTPViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var number: String? = null
    private var storedVerificationId = ""
    private var otpSent = false
    private var timeOut = false
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var verificationCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var countDownTimer: CountDownTimer
    private var verifySeller = false
    private var sellerShop = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        getArgs()
        initView()
        setListener()
        setObservers()
        number?.let { sendOtp(it) }
    }

    private fun getArgs() {
        number = intent.getStringExtra(AppConstants.PREFS_SELLER_MOBILE)
        verifySeller = intent.getBooleanExtra(AppConstants.SELLER_INVITE, false)
        sellerShop = intent.getStringExtra(AppConstants.SELLER_SHOP)

        println("TestingHar:"+verifySeller+" "+sellerShop+" "+number)
        println("Number testing" + number)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p)
        binding.textHeading.text = getString(R.string.otp_header) + " " + number
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        auth = FirebaseAuth.getInstance()
    }

    private fun setListener() {
        countDownTimer = object : CountDownTimer(10000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.textResendOtp.text = "Resend OTP (" + millisUntilFinished / 1000 + ")"
            }
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.textResendOtp.text = "Resend OTP"
                binding.textResendOtp.isEnabled = true
            }
        }

        verificationCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Verification Successful!", Toast.LENGTH_LONG)
                    .show()
                binding.editOtp.setText(p0.smsCode)
                signInWithPhoneAuthCredential(p0)
            }
            override fun onVerificationFailed(p0: FirebaseException) {
                progressDialog.dismiss()
                p0.printStackTrace()
                Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
                binding.editOtp.setText("")
                otpSent = false
                binding.textResendOtp.isEnabled = true
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                otpSent = true
                progressDialog.dismiss()
                storedVerificationId = verificationId
                resendToken = token
                binding.textResendOtp.isEnabled = false
                countDownTimer.start()
            }
            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                otpSent = false
                binding.textResendOtp.isEnabled = true
                timeOut = true
                Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
            }
        }
        binding.editOtp.setOnEditorActionListener { _, actionId, _ ->
            when(actionId){
                EditorInfo.IME_ACTION_DONE -> {
                    if (binding.editOtp.text.toString()
                            .isNotEmpty() && binding.editOtp.text.toString().length == 6
                    ) {
                        if (storedVerificationId.isNotEmpty()) {
                            val credential = PhoneAuthProvider.getCredential(
                                storedVerificationId,
                                binding.editOtp.text.toString()
                            )
                            signInWithPhoneAuthCredential(credential)
                        }
                    }
                    true
                }
                else -> false
            }
        }

        binding.buttonLogin.setOnClickListener {
            if (binding.editOtp.text.toString()
                    .isNotEmpty() && binding.editOtp.text.toString().length == 6
            ) {
                if (storedVerificationId.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(
                        storedVerificationId,
                        binding.editOtp.text.toString()
                    )
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
        binding.textResendOtp.setOnClickListener { v ->
            number?.let {
                number?.let { resendVerificationCode(number!!, resendToken) }
            }
        }
    }

    private fun setObservers() {
        viewModel.performLoginStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            progressDialog.dismiss()
                            val shopModelList = resource.data.data?.shopModelList
                            val userModel = resource.data.data?.userModel
                            if (userModel != null) {
                                preferencesHelper.saveUser(
                                    id = userModel.id,
                                    name = userModel.name,
                                    email = userModel.email,
                                    mobile = userModel.mobile,
                                    role = userModel.role,
                                    oauthId = userModel.oauthId,
                                    shop = Gson().toJson(shopModelList)
                                )
                                preferencesHelper.currentShop =
                                    shopModelList?.get(0)?.shopModel?.id!!
                                unloadKoinModules(networkModule)
                                loadKoinModules(networkModule)
                                startActivity(Intent(applicationContext, HomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Data not available in Server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
                        progressDialog.setMessage("Logging in...")
                        progressDialog.show()
                    }
                }
            }
        })


        viewModel.acceptInviteResponse.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            progressDialog.dismiss()
                            val shopModelList = resource.data.data?.shopModelList
                            val userModel = resource.data.data?.userModel
                            if (userModel != null) {
                                preferencesHelper.saveUser(
                                    id = userModel.id,
                                    name = userModel.name,
                                    email = userModel.email,
                                    mobile = userModel.mobile,
                                    role = userModel.role,
                                    oauthId = userModel.oauthId,
                                    shop = Gson().toJson(shopModelList)
                                )
                                preferencesHelper.currentShop =
                                    shopModelList?.get(0)?.shopModel?.id!!
                                unloadKoinModules(networkModule)
                                loadKoinModules(networkModule)
                                startActivity(Intent(applicationContext, HomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Data not available in Server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
                        progressDialog.setMessage("Logging in...")
                        progressDialog.show()
                    }
                }
            }
        })
    }

    private fun sendOtp(number: String) {
        progressDialog.setMessage("Sending OTP")
        progressDialog.show()
        timeOut = false
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            verificationCallBack
        )
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Logging in...")
        progressDialog.show()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    println("TestingHar2:$verifySeller $sellerShop $number")
                    val user = task.result?.user
                    preferencesHelper.oauthId = user?.uid
                    preferencesHelper.mobile = user?.phoneNumber?.substring(3)
                    val userModel = user?.uid?.let {
                        user.phoneNumber?.let { it1 ->
                            UserModel(
                                oauthId = it,
                                mobile = it1.substring(3)
                            )
                        }
                    }
                    if (!verifySeller) {
                        // Normal sign up
                        userModel?.let { viewModel.login(it) } ?: run {
                            Toast.makeText(applicationContext, "Login failed!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    else{
                        // accept invite sign Up
                        if(sellerShop.isNotEmpty() && sellerShop.matches(Regex("\\d+"))){
                            var shopModel = ShopModel(id = sellerShop.toInt())
                            var userShopModel =
                                userModel?.let { UserShopModel(shopModel= shopModel,userModel = it) }
                            userShopModel?.let { viewModel.acceptInvite(it) }
                        }
                        else{
                          Toast.makeText(this,"Accept Invite failed",Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    progressDialog.dismiss()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        (task.exception as FirebaseAuthInvalidCredentialsException).printStackTrace()
                        Toast.makeText(
                            applicationContext,
                            "Verification failed!",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.editOtp.setText("")
                    }
                }
                otpSent = false
                countDownTimer.cancel()
                binding.textResendOtp.isEnabled = true
            }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cancel process?")
            .setMessage("Are you sure want to cancel the OTP process?")
            .setPositiveButton("Yes") { dialog, which ->
                super.onBackPressed()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun resendVerificationCode(number: String, token: PhoneAuthProvider.ForceResendingToken) {
        timeOut = false
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            verificationCallBack,         // OnVerificationStateChangedCallbacks
            token // ForceResendingToken from callbacks
        )
    }
}
