package com.food.ordering.zinger.seller.ui.profile

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.databinding.ActivityProfileBinding
import com.food.ordering.zinger.seller.databinding.BottomSheetVerifyOtpBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: ProfileViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var dialogBinding: BottomSheetVerifyOtpBinding
    private lateinit var verificationCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var storedVerificationId: String
    private lateinit var dialog: BottomSheetDialog
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var otpVerified = false

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
                dialogBinding.textResendOtp.isEnabled = true
            }
        }

        binding.buttonUpdate.setOnClickListener {
            if (binding.editName.isEnabled)
                Toast.makeText(this, "Please confirm name change", Toast.LENGTH_LONG).show()
            else if (binding.editEmail.isEnabled)
                Toast.makeText(this, "Please confirm email change", Toast.LENGTH_LONG).show()
            else {
                val name = binding.editName.editableText.toString()
                val email = binding.editEmail.editableText.toString()
                val mobile = preferencesHelper.mobile
                if (name != preferencesHelper.name || email != preferencesHelper.email) {
                    viewModel.updateProfile(
                        UserModel(
                            id = preferencesHelper.id, name = name, email = email, mobile = mobile
                            , oauthId = preferencesHelper.oauthId
                        )
                    )
                }
            }
        }

        binding.imageEditName.setOnClickListener {
            if (!binding.editName.isEnabled)
                binding.imageEditName.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditName.setImageResource(R.drawable.ic_edit)
            binding.editName.isEnabled = !(binding.editName.isEnabled)
        }

        binding.imageEditEmail.setOnClickListener {
            if (!binding.editEmail.isEnabled)
                binding.imageEditEmail.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditEmail.setImageResource(R.drawable.ic_edit)
            binding.editEmail.isEnabled = !binding.editEmail.isEnabled
        }

        binding.editMobile.setOnClickListener {
            showOtpVerificationBottomSheet(preferencesHelper.mobile!!)
        }

        verificationCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Verification Successful!", Toast.LENGTH_LONG)
                    .show()
                dialogBinding.editOtp.setText(p0.smsCode)
                viewModel.signInWithPhoneAuthCredential(p0, this@ProfileActivity)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                progressDialog.dismiss()
                p0.printStackTrace()
                Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
                dialogBinding.editOtp.setText("")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
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
                        if (otpVerified) {
                            preferencesHelper.mobile = preferencesHelper.tempMobile
                            preferencesHelper.oauthId = preferencesHelper.tempOauthId
                            otpVerified = false
                        }
                        binding.editMobile.setText(preferencesHelper.mobile)
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
                        otpVerified = true
                        binding.editMobile.setText(preferencesHelper.tempMobile)
                        countDownTimer.cancel()
                        val updateUserRequest = UserModel(
                            preferencesHelper.id,
                            preferencesHelper.email,
                            preferencesHelper.tempMobile,
                            preferencesHelper.name,
                            preferencesHelper.tempOauthId
                        )
                        viewModel.updateProfile(updateUserRequest)
                        dialog.let {
                            dialog.dismiss()
                        }
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(this, "OTP verification failed ", Toast.LENGTH_LONG).show()
                        countDownTimer.cancel()
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showOtpVerificationBottomSheet(number: String) {
        dialogBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.bottom_sheet_verify_otp,
                null,
                false
            )
        dialogBinding.editMobile.setText(number)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.textResendOtp.setOnClickListener {
            Toast.makeText(this, "OTP resent", Toast.LENGTH_LONG).show()
            resendVerificationCode(dialogBinding.editMobile.text.toString(), resendToken)
        }

        dialogBinding.editOtp.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    verifyOtpRequest(dialogBinding)
                    true
                }
                else -> false
            }
        }

        dialogBinding.buttonVerify.setOnClickListener {
            if (dialogBinding.layoutOtp.visibility == View.GONE) {
                if (dialogBinding.editMobile.text.toString() == preferencesHelper.mobile) {
                    Toast.makeText(applicationContext, "Mobile number is same!", Toast.LENGTH_SHORT).show()
                } else if (dialogBinding.editMobile.text.toString().length != 10 && dialogBinding.editMobile.text.toString().matches(Regex("\\d+"))) {
                    Toast.makeText(applicationContext, "Incorrect Format!", Toast.LENGTH_SHORT).show()
                } else {
                    dialogBinding.layoutOtp.visibility = View.VISIBLE
                    dialogBinding.buttonVerify.text = "Verify OTP"
                    dialogBinding.editMobile.isEnabled = false
                    sendOtp(dialogBinding.editMobile.text.toString())
                }
            } else {
                verifyOtpRequest(dialogBinding)
            }

        }
    }

    private fun verifyOtpRequest(dialogBinding: BottomSheetVerifyOtpBinding) {
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

    private fun sendOtp(number: String) {
        progressDialog.setMessage("Sending OTP")
        progressDialog.show()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91$number", // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            verificationCallBack
        )
    }

    private fun resendVerificationCode(number: String, token: PhoneAuthProvider.ForceResendingToken) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91$number",        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            verificationCallBack,         // OnVerificationStateChangedCallbacks
            token // ForceResendingToken from callbacks
        )
    }
}
