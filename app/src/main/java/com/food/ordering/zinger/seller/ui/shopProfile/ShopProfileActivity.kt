package com.food.ordering.zinger.seller.ui.shopProfile

import android.app.Activity
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.ConfigurationModel
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.data.model.ShopModel
import com.food.ordering.zinger.seller.databinding.ActivityShopProfileBinding
import com.food.ordering.zinger.seller.ui.display.DisplayActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.food.ordering.zinger.seller.utils.CommonUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*;
import kotlin.collections.ArrayList

class ShopProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopProfileBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: ShopProfileViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog
    private var shopConfig: ShopConfigurationModel? = null
    private var shopCoverImageAdapter: ShopCoverImageAdapter? = null
    private var imageList: ArrayList<String> = ArrayList()
    private var isShopLogoClicked = false
    private var isShopCoverImageClicked = false
    private var mStorageRef: StorageReference? = null
    private lateinit var updateConfigurationModel: ConfigurationModel
    private lateinit var successSnackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_profile)
        initView()
        setListener()
        setObserver()
        viewModel.getShopDetail(preferencesHelper.currentShop)
    }

    private fun initView() {
        mStorageRef = FirebaseStorage.getInstance().reference
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_profile)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        successSnackbar = Snackbar.make(binding.root, " ", Snackbar.LENGTH_INDEFINITE)
    }

    private fun updateUI() {
        shopConfig = preferencesHelper.getShop()?.firstOrNull {
            it.shopModel.id == preferencesHelper.currentShop
        }

        imageList.clear()
        shopConfig?.shopModel?.coverUrls?.let { imageList.addAll(it) }

        shopCoverImageAdapter =
            ShopCoverImageAdapter(
                imageList,
                preferencesHelper.role,
                object : ShopCoverImageAdapter.OnItemClickListener {
                    override fun onItemClick(item: List<String>?, position: Int) {
                        val intent = Intent(applicationContext, DisplayActivity::class.java)
                        intent.putExtra(AppConstants.DISPLAY_IMAGE_DETAIL, imageList[position])
                        startActivity(intent)
                    }
                    override fun onDeleteClick(item: List<String>?, position: Int) {
                        imageList.removeAt(position)
                        shopCoverImageAdapter?.notifyDataSetChanged()
                    }

                })
        binding.recyclerCoverPhoto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCoverPhoto.adapter = shopCoverImageAdapter
        binding.editName.setText(shopConfig?.shopModel?.name)
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
        val sdf2 = SimpleDateFormat("hh:mm a", Locale.US)
        binding.textOpeningTime.text = sdf2.format(sdf.parse(shopConfig?.shopModel?.openingTime))
        binding.textClosingTime.text = sdf2.format(sdf.parse(shopConfig?.shopModel?.closingTime))
        Picasso.get().load(shopConfig?.shopModel?.photoUrl).placeholder(R.drawable.ic_shop)
            .into(binding.imageLogo)
        binding.switchOrders.isChecked = shopConfig?.configurationModel?.isOrderTaken == 1
        binding.switchDelivery.isChecked = shopConfig?.configurationModel?.isDeliveryAvailable == 1

        if (binding.switchOrders.isChecked)
            binding.switchOrders.thumbTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.switchSelected
                )
            )
        else
            binding.switchOrders.thumbTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.accent))

        if (binding.switchDelivery.isChecked)
            binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.switchSelected
                )
            )
        else
            binding.switchDelivery.thumbTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.accent))


        binding.editMerchantId.setText(shopConfig?.configurationModel?.merchantId.toString())
        binding.editName.setSelection(binding.editName.text.toString().length)
        binding.editDeliveryPrice.setText(
            shopConfig?.configurationModel?.deliveryPrice?.toInt().toString()
        )

        preferencesHelper.role?.let {
            if (it == AppConstants.ROLE.SELLER.name || it == AppConstants.ROLE.DELIVERY.name) {
                binding.imageEditName.visibility = View.GONE
                binding.imageEditDeliveryPrice.visibility = View.GONE
                binding.imageEditOpeningTime.visibility = View.GONE
                binding.imageEditClosingTime.visibility = View.GONE
                binding.textLogo.visibility = View.GONE
                binding.textCoverPhoto.visibility = View.GONE

                binding.imageEditName.isEnabled = false
                binding.imageEditDeliveryPrice.isEnabled = false
                binding.imageEditOpeningTime.isEnabled = false
                binding.imageEditClosingTime.isEnabled = false
                binding.textLogo.isEnabled = false
                binding.textCoverPhoto.isEnabled = false

                binding.switchOrders.isClickable = false
                binding.switchDelivery.isClickable = false
                binding.buttonUpdate.visibility = View.GONE
                binding.buttonUpdate.isEnabled = false
            }

            if(it==AppConstants.ROLE.SHOP_OWNER.name){
                binding.textMerchantId.visibility =View.VISIBLE
                binding.layoutMerchantId.visibility = View.VISIBLE
            }
        }
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
        binding.switchDelivery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchSelected
                    )
                )
            else
                binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.accent
                    )
                )
        }
        binding.switchOrders.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                binding.switchOrders.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchSelected
                    )
                )
            else
                binding.switchOrders.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.accent
                    )
                )
        }

        binding.buttonUpdate.setOnClickListener {
            if (binding.editName.isEnabled) {
                Toast.makeText(this, "Please confirm name change", Toast.LENGTH_LONG).show()
            } else if (binding.editDeliveryPrice.isEnabled) {
                Toast.makeText(this, "Please confirm delivery price change", Toast.LENGTH_LONG).show()
            }else if (binding.layoutMerchantId.visibility == View.VISIBLE && binding.editMerchantId.isEnabled) {
                Toast.makeText(this, "Please confirm merchant Id change", Toast.LENGTH_LONG).show()
            }else {
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                val sdf2 = SimpleDateFormat("hh:mm a", Locale.US)
                val openingTime = sdf.format(sdf2.parse(binding.textOpeningTime.text.toString()))
                val closingTime = sdf.format(sdf2.parse(binding.textClosingTime.text.toString()))
                var mid = " "
                if(binding.layoutMerchantId.visibility == View.VISIBLE){
                    mid = binding.editMerchantId.text.toString()
                }else{
                    shopConfig?.configurationModel?.merchantId?.let {
                        mid = it
                    }
                }
                val shopModel = ShopModel(
                    photoUrl = if (photoUrl.isNullOrEmpty()) shopConfig?.shopModel?.photoUrl else photoUrl,
                    closingTime = closingTime,
                    openingTime = openingTime,
                    name = binding.editName.text.toString(),
                    coverUrls = imageList,
                    mobile = shopConfig?.shopModel?.mobile,
                    id = shopConfig?.shopModel?.id
                )
                updateConfigurationModel = ConfigurationModel(
                    deliveryPrice = binding.editDeliveryPrice.text.toString().toDouble(),
                    isDeliveryAvailable = if (binding.switchDelivery.isChecked) 1 else 0,
                    isOrderTaken = if (binding.switchOrders.isChecked) 1 else 0,
                    merchantId = mid,
                    shopModel = shopModel
                )
                viewModel.updateShopProfile(updateConfigurationModel)
            }
        }

        binding.imageEditName.setOnClickListener {
            if (!binding.editName.isEnabled)
                binding.imageEditName.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditName.setImageResource(R.drawable.ic_edit)
            binding.editName.isEnabled = !(binding.editName.isEnabled)
        }
        binding.imageEditDeliveryPrice.setOnClickListener {
            if (!binding.editDeliveryPrice.isEnabled)
                binding.imageEditDeliveryPrice.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditDeliveryPrice.setImageResource(R.drawable.ic_edit)
            binding.editDeliveryPrice.isEnabled = !(binding.editDeliveryPrice.isEnabled)
        }
        binding.imageEditMerchantId.setOnClickListener {
            if (!binding.editMerchantId.isEnabled)
                binding.imageEditMerchantId.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditMerchantId.setImageResource(R.drawable.ic_edit)
            binding.editMerchantId.isEnabled = !(binding.editMerchantId.isEnabled)
        }
        binding.imageEditOpeningTime.setOnClickListener {
            val openingTime =
                SimpleDateFormat("HH:mm:ss", Locale.US).parse(shopConfig?.shopModel?.openingTime)
            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { view, hourOfDay, minute ->
                    val time = CommonUtils.TimeConversion24to12(hourOfDay, minute)
                    binding.textOpeningTime.text = time
                }, openingTime.hours, openingTime.minutes, false
            )
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    android.R.color.tab_indicator_text
                )
            )
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
        }
        binding.imageEditClosingTime.setOnClickListener {
            val closingTime =
                SimpleDateFormat("HH:mm:ss", Locale.US).parse(shopConfig?.shopModel?.closingTime)
            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { view, hourOfDay, minute ->
                    val time = CommonUtils.TimeConversion24to12(hourOfDay, minute)
                    binding.textClosingTime.text = time
                }, closingTime.hours, closingTime.minutes, false
            )
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    android.R.color.tab_indicator_text
                )
            )
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
        }
        binding.textLogo.setOnClickListener {
            isShopLogoClicked = true
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .cropSquare()
                .start()
        }
        binding.textCoverPhoto.setOnClickListener {
            isShopCoverImageClicked = true;
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .crop(16f, 9f)
                .start()
        }
        binding.imageLogo.setOnClickListener {
            val intent = Intent(applicationContext, DisplayActivity::class.java)
            val photoUrl =
                if (photoUrl.isNullOrEmpty()) shopConfig?.shopModel?.photoUrl else photoUrl
            intent.putExtra(AppConstants.DISPLAY_IMAGE_DETAIL, photoUrl)
            startActivity(intent)
        }


    }

    var photoUrl: String? = null
    private fun setObserver() {
        viewModel.performUploadImageStatus.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        resource.data?.let {
                            if (isShopCoverImageClicked) {
                                imageList.add(resource.data)
                                shopCoverImageAdapter?.notifyDataSetChanged()
                                isShopCoverImageClicked = !isShopCoverImageClicked
                            } else if (isShopLogoClicked) {
                                photoUrl = resource.data
                                Picasso.get().load(resource.data)
                                    .placeholder(R.drawable.ic_shop)
                                    .into(binding.imageLogo)
                                isShopLogoClicked = !isShopLogoClicked
                            }
                        }
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred \n" + resource.message,
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
                        progressDialog.setMessage("Updating...")
                        progressDialog.show()
                    }
                }
            }
        })

        viewModel.performUpdateShopProfileStatus.observe(
            this,
            androidx.lifecycle.Observer { resource ->
                if (resource != null) {
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            preferencesHelper.getShop()?.let { shopConfigurationList ->
                                for (i in shopConfigurationList)
                                    if (i.shopModel.id == updateConfigurationModel.shopModel?.id) {
                                        i.shopModel = updateConfigurationModel.shopModel!!
                                        i.configurationModel = updateConfigurationModel
                                    }
                                preferencesHelper.shop = Gson().toJson(shopConfigurationList)
                            }
                            progressDialog.dismiss()
                            successSnackbar.setText("Shop Profile Updated")
                            successSnackbar.show()
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
                                    "Update Failed Try again later",
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


        viewModel.getShopDetailResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        resource.data?.data?.let { latestShopData ->
                            preferencesHelper.getShop()?.let { shopConfigurationList ->

                                for (i in shopConfigurationList) {
                                    if (i.shopModel.id == preferencesHelper.currentShop) {
                                        i.shopModel = latestShopData.shopModel
                                        i.configurationModel = latestShopData.configurationModel
                                        i.ratingModel = latestShopData.ratingModel
                                    }
                                }
                                preferencesHelper.shop = Gson().toJson(shopConfigurationList)
                                updateUI()
                            }
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
                                "Failed to fetch data, Try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Fetching shop data...")
                        progressDialog.show()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePicker.REQUEST_CODE) {
                val fileUri = data?.data
                val file: File? = ImagePicker.getFile(data)
                var storageReference: StorageReference? = null
                if (isShopLogoClicked) {
                    storageReference =
                        mStorageRef?.child("profileImage/" + shopConfig?.shopModel?.id + "/" + file?.name + Calendar.getInstance().time)

                } else if (isShopCoverImageClicked) {
                    storageReference =
                        mStorageRef?.child("coverImage/" + shopConfig?.shopModel?.id + "/" + file?.name + Calendar.getInstance().time)
                }
                if (storageReference != null) {
                    if (fileUri != null) {
                        viewModel.uploadPhotoToFireBase(storageReference, fileUri)
                    }
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
