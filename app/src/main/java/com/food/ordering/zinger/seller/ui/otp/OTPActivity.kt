package com.food.ordering.zinger.seller.ui.otp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.food.ordering.zinger.seller.MainActivity
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.databinding.ActivityOTPBinding
import com.food.ordering.zinger.seller.di.networkModule
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
    private var otp = ""
    private var storedVerificationId = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

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
        println("Number testing"+number)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        auth = FirebaseAuth.getInstance()
    }

    private fun setListener() {
        binding.buttonLogin.setOnClickListener {
            if (binding.editOtp.text.toString().isNotEmpty() && binding.editOtp.text.toString().length == 6) {
                if (storedVerificationId.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId, binding.editOtp.text.toString())
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setObservers() {
        viewModel.performLoginStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            progressDialog.dismiss()
                            if (resource.data.code==1163) {
                                unloadKoinModules(networkModule)
                                loadKoinModules(networkModule)
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val shopModelList =resource.data.data?.shopModelList
                                val userModel = resource.data.data?.userModel
                                if(userModel!=null){
                                    preferencesHelper.saveUser(
                                        id = userModel.id,
                                        name = userModel.name,
                                        email = userModel.email,
                                        mobile = userModel.mobile,
                                        role = userModel.role,
                                        oauthId = userModel.oauthId,
                                        shop = Gson().toJson(shopModelList)
                                    )
                                }
                                unloadKoinModules(networkModule)
                                loadKoinModules(networkModule)
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
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
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Verification Successful!", Toast.LENGTH_LONG).show()
                    binding.editOtp.setText(p0.smsCode)
                    signInWithPhoneAuthCredential(p0)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    progressDialog.dismiss()
                    p0.printStackTrace()
                    Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
                    binding.editOtp.setText("")
                    //TODO OTP shake animation
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    progressDialog.dismiss()
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    //Log.d(TAG, "onCodeSent:$verificationId")

                    // Save verification ID and resending token so we can use them later
                    storedVerificationId = verificationId
                    resendToken = token
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    //TODO enable resend
                    //
                }
            }) // OnVerificationStateChangedCallbacks
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Logging in...")
        progressDialog.show()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCredential:success")
                    progressDialog.dismiss()
                    val user = task.result?.user
                    preferencesHelper.oauthId = user?.uid
                    preferencesHelper.mobile = user?.phoneNumber?.substring(3)
                    preferencesHelper.role = "SHOP_OWNER"
                    val loginRequest = user?.uid?.let { user.phoneNumber?.let { it1 -> UserModel(oauthId = it, mobile = it1.substring(3)) } }
                    loginRequest?.let { viewModel.login(it) }?:run{
                        Toast.makeText(applicationContext,"Login failed!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    progressDialog.dismiss()
                    // Sign in failed, display a message and update the UI
                    //Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        (task.exception as FirebaseAuthInvalidCredentialsException).printStackTrace()
                        Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
                        binding.editOtp.setText("")
                    }
                }
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
}
