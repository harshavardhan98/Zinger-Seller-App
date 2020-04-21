package com.food.ordering.zinger.seller.ui.shopProfile

import android.app.Activity
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.ConfigurationModel
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.data.model.ShopImageDataModel
import com.food.ordering.zinger.seller.data.model.ShopModel
import com.food.ordering.zinger.seller.databinding.ActivityShopProfileBinding
import com.food.ordering.zinger.seller.utils.CommonUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.bind
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
    private var currentShopLogoUri: Uri? = null
    private var deleteImageList: ArrayList<String> = ArrayList()
    private var mStorageRef: StorageReference? = null
    private lateinit var updateConfigurationModel: ConfigurationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_profile)
        // todo ask storage permission
        // todo api request not working and going to black screen
        initView()
        setListener()
        setObserver()

    }

    private fun initView() {

        mStorageRef = FirebaseStorage.getInstance().getReference();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_profile)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)


        shopConfig = preferencesHelper.getShop()?.firstOrNull {
            it.shopModel.id == preferencesHelper.currentShop
        }


        imageList.clear()
        shopConfig?.shopModel?.coverUrls?.let { imageList.addAll(it) }

        shopCoverImageAdapter =
            ShopCoverImageAdapter(imageList, object : ShopCoverImageAdapter.OnItemClickListener {

                override fun onItemClick(shopImageList: List<String>?, position: Int) {
                    Toast.makeText(applicationContext, "testing " + position, Toast.LENGTH_SHORT)
                        .show()
                    // todo go to a plain screen
                }

                override fun onDeleteClick(shopImageList: List<String>?, position: Int) {
                    imageList.removeAt(position)
                    shopCoverImageAdapter?.notifyDataSetChanged()
                }

            })
        binding.recyclerCoverPhoto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCoverPhoto.adapter = shopCoverImageAdapter

        binding.editName.setText(shopConfig?.shopModel?.name)
        var sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
        var sdf2 = SimpleDateFormat("hh:mm a", Locale.US)
        binding.textOpeningTime.text = sdf2.format(sdf.parse(shopConfig?.shopModel?.openingTime))
        binding.textClosingTime.text = sdf2.format(sdf.parse(shopConfig?.shopModel?.closingTime))
        Picasso.get().load(shopConfig?.shopModel?.photoUrl).placeholder(R.drawable.ic_shop)
            .into(binding.imageLogo)
        binding.switchOrders.isChecked = shopConfig?.configurationModel?.isOrderTaken == 1
        binding.switchDelivery.isChecked = shopConfig?.configurationModel?.isDeliveryAvailable == 1
        binding.editName.setSelection(binding.editName.text.toString().length)
        binding.editDeliveryPrice.setText(shopConfig?.configurationModel?.deliveryPrice.toString())

    }

    private fun setListener() {
        binding.buttonUpdate.setOnClickListener(View.OnClickListener {

            var openingTime = binding.textOpeningTime.text.toString().split(Regex(" "), 0)
            var closingTime = binding.textClosingTime.text.toString().split(Regex(" "), 0)

            val shopModel = ShopModel(
                photoUrl = if(photoUrl.isNullOrEmpty())shopConfig?.shopModel?.photoUrl else photoUrl,
                closingTime = closingTime.get(0) + ":00",
                openingTime = openingTime.get(0) + ":00",
                name = binding.editName.text.toString(),
                coverUrls = imageList,
                mobile = shopConfig?.shopModel?.mobile,
                id = shopConfig?.shopModel?.id
            )


            updateConfigurationModel = ConfigurationModel(
                deliveryPrice = binding.editDeliveryPrice.text.toString().toDouble(),
                isDeliveryAvailable = if (binding.switchOrders.isChecked) 1 else 0,
                isOrderTaken = if (binding.switchDelivery.isChecked) 1 else 0,
                shopModel = shopModel
            )

            viewModel.updateShopProfile(updateConfigurationModel)

        })

        binding.imageEditName.setOnClickListener(View.OnClickListener {
            if (!binding.editName.isEnabled)
                binding.imageEditName.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditName.setImageResource(R.drawable.ic_edit)
            binding.editName.isEnabled = !(binding.editName.isEnabled)
        })

        binding.imageEditDeliveryPrice.setOnClickListener(View.OnClickListener {
            if (!binding.editDeliveryPrice.isEnabled)
                binding.imageEditDeliveryPrice.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditDeliveryPrice.setImageResource(R.drawable.ic_edit)
            binding.editDeliveryPrice.isEnabled = !(binding.editDeliveryPrice.isEnabled)
        })


        binding.textOpeningTime.setOnClickListener(View.OnClickListener {

            var openingTime =
                SimpleDateFormat("HH:mm:ss", Locale.US).parse(shopConfig?.shopModel?.openingTime)


            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { view, hourOfDay, minute ->
                    var time = CommonUtils.TimeConversion24to12(hourOfDay, minute)
                    binding.textOpeningTime.setText(time)
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
        })

        binding.textClosingTime.setOnClickListener(View.OnClickListener {

            var closingTime =
                SimpleDateFormat("HH:mm:ss", Locale.US).parse(shopConfig?.shopModel?.closingTime)

            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { view, hourOfDay, minute ->
                    var time = CommonUtils.TimeConversion24to12(hourOfDay, minute)
                    binding.textClosingTime.setText(time)
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
        })


        binding.textLogo.setOnClickListener { v ->
            isShopLogoClicked = true
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .start()
        }

        binding.textCoverPhoto.setOnClickListener { v ->
            isShopCoverImageClicked = true;
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .start()
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
                            "Try again!! Error Occurred",
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
                                        i.shopModel= updateConfigurationModel.shopModel!!
                                        i.configurationModel =updateConfigurationModel
                                    }

                                preferencesHelper.shop = Gson().toJson(shopConfigurationList)
                            }

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
