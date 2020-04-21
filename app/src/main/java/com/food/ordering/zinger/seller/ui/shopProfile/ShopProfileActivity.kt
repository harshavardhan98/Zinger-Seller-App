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
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.data.model.ShopImageDataModel
import com.food.ordering.zinger.seller.databinding.ActivityShopProfileBinding
import com.food.ordering.zinger.seller.utils.CommonUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShopProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopProfileBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: ShopProfileViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog
    private var shopConfig: ShopConfigurationModel? = null
    private var shopCoverImageAdapter: ShopCoverImageAdapter? = null
    private var imageList: ArrayList<ShopImageDataModel> = ArrayList()
    private var isShopLogoClicked = false
    private var isShopCoverImageClicked = false
    private var currentShopLogoUri: Uri? = null
    private var deleteImageList: ArrayList<String> = ArrayList()
    private var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_profile)
        // todo ask storage permission
        // todo add delivery price change
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


        shopConfig?.shopModel?.coverUrls?.let {
            it.forEach { it1 ->
                imageList.add(
                    ShopImageDataModel(
                        imageLink = it1
                    )
                )
            }
        }

        shopCoverImageAdapter =
            ShopCoverImageAdapter(imageList, object : ShopCoverImageAdapter.OnItemClickListener {

                override fun onItemClick(shopImageList: List<ShopImageDataModel>?, position: Int) {
                    Toast.makeText(applicationContext, "testing " + position, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onDeleteClick(
                    shopImageList: List<ShopImageDataModel>?,
                    position: Int
                ) {
                    imageList.get(position).imageLink?.let { deleteImageList.add(it) }
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
    }

    private fun setListener() {
        binding.buttonUpdate.setOnClickListener(View.OnClickListener {
            /*
            *  1. upload the photo to the firebase storage
            *  2. delete the image link from the firebase -> skipping for now
            *  3. update the latest shopConfiguration ->
            *  4. update the shared preference
            * */

            var fileUploadResult = true

            currentShopLogoUri.let {
                var storageReference =
                    mStorageRef?.child("profileImage/" + shopConfig?.shopModel?.id + "/" + it?.toFile()?.name + Calendar.getInstance().time)

                it?.let { it1 ->
                    storageReference?.putFile(it1)
                        ?.addOnSuccessListener{
                            val result = it.metadata!!.reference!!.downloadUrl;
                            result.addOnSuccessListener {
                                val imageLink = it.toString()
                                shopConfig?.shopModel?.photoUrl=imageLink
                            }
                        }
                        ?.addOnFailureListener {
                            fileUploadResult = false
                        }
                }
            }

            imageList.forEach { item ->
                item.imageUri?.let {
                    var storageReference =
                        mStorageRef?.child("coverImage/" + shopConfig?.shopModel?.id + "/" + it.toFile().name + Calendar.getInstance().time)

                    storageReference?.putFile(it)
                        ?.addOnSuccessListener {
                            val result = it.metadata!!.reference!!.downloadUrl;
                            result.addOnSuccessListener {
                                val imageLink = it.toString()
                                println(imageLink)
                            }
                        }
                        ?.addOnFailureListener {
                            fileUploadResult = false
                        }
                }
            }

            if (fileUploadResult) {
                shopConfig?.shopModel?.name = binding.editName.text.toString()
                shopConfig?.configurationModel?.isDeliveryAvailable =
                    if (binding.switchOrders.isChecked) 1 else 0
                shopConfig?.configurationModel?.isOrderTaken =
                    if (binding.switchDelivery.isChecked) 1 else 0
                var openingTime = binding.textOpeningTime.text.toString().split(Regex(" "), 0)
                var closingTime = binding.textClosingTime.text.toString().split(Regex(" "), 0)
                shopConfig?.configurationModel?.shopModel?.openingTime = openingTime.get(0) + ":00"
                shopConfig?.configurationModel?.shopModel?.closingTime = closingTime.get(0) + ":00"

                shopConfig?.configurationModel?.shopModel?.coverUrls?.clear()
                imageList.forEach {
                    it?.imageLink?.let { it1 ->
                        shopConfig?.configurationModel?.shopModel?.coverUrls?.add(
                            it1
                        )
                    }
                }
            }

            shopConfig?.configurationModel?.shopModel = shopConfig?.shopModel
            println("testing")

        })

        binding.imageEditName.setOnClickListener(View.OnClickListener {
            if (!binding.editName.isEnabled)
                binding.imageEditName.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditName.setImageResource(R.drawable.ic_edit)
            binding.editName.isEnabled = !(binding.editName.isEnabled)
        })

        binding.switchOrders.setOnClickListener(View.OnClickListener { v ->
            shopConfig?.configurationModel?.isOrderTaken =
                if (binding.switchOrders.isChecked) 1 else 0
        })

        binding.switchDelivery.setOnClickListener(View.OnClickListener { v ->
            shopConfig?.configurationModel?.isDeliveryAvailable =
                if (binding.switchDelivery.isChecked) 1 else 0
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

    private fun setObserver() {
        /*viewModel.performUpdateProfileStatus.observe(this, Observer { resource ->
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
        })*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == ImagePicker.REQUEST_CODE) {

                val fileUri = data?.data
                val file: File? = ImagePicker.getFile(data)
                val filePath: String? = ImagePicker.getFilePath(data)

                if (isShopLogoClicked) {
                    currentShopLogoUri = fileUri
                    binding.imageLogo.setImageURI(fileUri)
                    isShopLogoClicked = !isShopLogoClicked
                } else if (isShopCoverImageClicked) {
                    // todo add to the cover image list
                    var shopImageDataModel = ShopImageDataModel(imageUri = fileUri)
                    imageList.add(shopImageDataModel)
                    shopCoverImageAdapter?.notifyDataSetChanged()
                    isShopCoverImageClicked = !isShopCoverImageClicked
                }
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
