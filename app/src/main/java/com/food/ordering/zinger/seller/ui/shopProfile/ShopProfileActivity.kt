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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.data.model.ShopImageDataModel
import com.food.ordering.zinger.seller.databinding.ActivityShopProfileBinding
import com.food.ordering.zinger.seller.utils.CommonUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
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
    private var shopCoverImageAdapter: ShopCoverImageAdapter? =null
    private var imageList:ArrayList<ShopImageDataModel> = ArrayList()
    private var isShopLogoClicked = false
    private var isShopCoverImageClicked = false
    private var currentShopLogoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_profile)

        initView()
        setListener()
        setObserver()
    }

    private fun initView() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_profile)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)


        shopConfig = preferencesHelper.getShop()?.firstOrNull {
            it.shopModel.id == preferencesHelper.currentShop
        }


        shopConfig?.shopModel?.coverUrls?.let { it.forEach { it1 -> imageList.add(ShopImageDataModel(imageLink = it1)) } }

        shopCoverImageAdapter = ShopCoverImageAdapter(imageList,object : ShopCoverImageAdapter.OnItemClickListener{

            override fun onItemClick(item: List<ShopImageDataModel>?, position: Int) {
                Toast.makeText(applicationContext,"testing "+position,Toast.LENGTH_SHORT).show()
            }

            override fun onUpdateClick(item: List<ShopImageDataModel>?, position: Int) {

            }

        })
        binding.recyclerCoverPhoto.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
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

            var closingTime = SimpleDateFormat("HH:mm:ss", Locale.US).parse(shopConfig?.shopModel?.closingTime)

            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { view, hourOfDay, minute ->
                    var time = CommonUtils.TimeConversion24to12(hourOfDay, minute)
                    binding.textClosingTime.setText(time)
                }, closingTime.hours, closingTime.minutes, false
            )
            timePickerDialog.show()
        })


        binding.textLogo.setOnClickListener { v->
                isShopLogoClicked = true
                ImagePicker.with(this)
                            .galleryOnly()
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

            if(requestCode==ImagePicker.REQUEST_CODE){

                val fileUri = data?.data
                val file: File? = ImagePicker.getFile(data)
                val filePath: String? = ImagePicker.getFilePath(data)

                if(isShopLogoClicked){
                    currentShopLogoUri=fileUri

//                    Picasso.get().load(filePath)
//                                 .placeholder(R.drawable.ic_shop)
//                                 .into(binding.imageLogo)
                    binding.imageLogo.setImageURI(fileUri)

                    isShopLogoClicked = !isShopLogoClicked
                }
                else if(isShopCoverImageClicked){
                    // todo add to the cover image list
                    var shopImageDataModel = ShopImageDataModel(imageUri= fileUri)
                    imageList.add(shopImageDataModel)
                    shopCoverImageAdapter?.notifyDataSetChanged()
                    isShopCoverImageClicked=!isShopCoverImageClicked
                }
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
