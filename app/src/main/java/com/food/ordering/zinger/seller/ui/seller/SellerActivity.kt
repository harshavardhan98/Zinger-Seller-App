package com.food.ordering.zinger.seller.ui.seller

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.ShopModel
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.data.model.UserShopModel
import com.food.ordering.zinger.seller.databinding.ActivitySellerBinding
import com.food.ordering.zinger.seller.databinding.BottomSheetInviteSellerBinding
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SellerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellerBinding
    private var userModelList: ArrayList<UserModel> = ArrayList()
    private lateinit var progressDialog: ProgressDialog
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: SellerViewModel by viewModel()
    private lateinit var dialogBinding: BottomSheetInviteSellerBinding
    private lateinit var sellerAdapter: SellerAdapter
    private lateinit var dialog : BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)
        initView()
        setListeners()
        setObservers()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_seller)
        progressDialog = ProgressDialog(this)
        dialog = BottomSheetDialog(this)
    }

    override fun onResume() {
        super.onResume()
        setUpRecyclerView()
    }

    private fun setListeners() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.textInviteSeller.setOnClickListener {
            showAddSellerBottomSheet()
        }
    }

    private fun setObservers() {
        viewModel.getSellerResponse.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        userModelList.clear()
                        resource.data?.data?.let {
                            progressDialog.dismiss()
                            userModelList.addAll(it)
                        }
                        sellerAdapter.notifyDataSetChanged()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
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
                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        userModelList.clear()
                        sellerAdapter.notifyDataSetChanged()
                        Toast.makeText(applicationContext, "No new Sellers found",Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Fetching Sellers...")
                        progressDialog.show()
                    }
                }
            }
        })

        viewModel.deleteSellerResponse.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Seller deleted", Toast.LENGTH_LONG).show()
                        viewModel.getSeller(preferencesHelper.currentShop.toString())
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
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
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Deleting Item...")
                        progressDialog.show()
                    }
                }
            }
        })

        viewModel.inviteSellerResponse.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Invite sent", Toast.LENGTH_LONG).show()
                        viewModel.getSeller(preferencesHelper.currentShop.toString())
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
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
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Inviting a seller...")
                        progressDialog.show()
                    }
                }
            }
        })

        viewModel.deleteInviteResponse.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Invite deleted", Toast.LENGTH_LONG).show()
                        viewModel.getSeller(preferencesHelper.currentShop.toString())
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
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
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Deleting Invite...")
                        progressDialog.show()
                    }
                }
            }
        })
    }


    private fun setUpRecyclerView() {
        sellerAdapter =
            SellerAdapter(userModelList, object : SellerAdapter.OnItemClickListener {
                override fun onDeleteClick(user: UserModel?, position: Int) {
                    user?.id?.let {
                        if(it!=0){
                            viewModel.deleteSeller(preferencesHelper.currentShop, it)
                        }
                        else{
                            val userModel = UserModel(mobile = user.mobile,role = user.role)
                            val shopModel = ShopModel(id = preferencesHelper.currentShop)
                            viewModel.deleteInvite(UserShopModel(shopModel,userModel))
                        }
                    }
                }
            })

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerSeller.layoutManager = layoutManager
        binding.recyclerSeller.adapter = sellerAdapter
        viewModel.getSeller(preferencesHelper.currentShop.toString())

    }

    private fun showAddSellerBottomSheet() {
        dialogBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.bottom_sheet_invite_seller,
                null,
                false
            )

        dialogBinding.buttonAddSeller.setOnClickListener {
            val mobile = dialogBinding.editMobile.text.toString()
            var role = ""
            when {
                dialogBinding.radioButtonDelivery.isChecked -> role = AppConstants.ROLE.DELIVERY.name
                dialogBinding.radioButtonSeller.isChecked -> role = AppConstants.ROLE.SELLER.name
                dialogBinding.radioButtonShopOwner.isChecked -> role = AppConstants.ROLE.SHOP_OWNER.name
            }
            if (mobile.length != 10 || !mobile.matches(Regex("\\d+")))
                Toast.makeText(this, "Incorrect mobile format", Toast.LENGTH_LONG).show()
            else if (role == AppConstants.ROLE.DELIVERY.name || role == AppConstants.ROLE.SELLER.name || role == AppConstants.ROLE.SHOP_OWNER.name) {
                val shopModel = ShopModel(id = preferencesHelper.currentShop)
                val userModel = UserModel(mobile = mobile, role = role)
                val userShopModel = UserShopModel(shopModel, userModel)
                viewModel.inviteSeller(userShopModel)
                dialog.dismiss()
            }
            else {
                Toast.makeText(this, "Choose a role", Toast.LENGTH_LONG).show()
            }
        }
        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }

}
