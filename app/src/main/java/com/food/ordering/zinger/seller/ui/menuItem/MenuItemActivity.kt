package com.food.ordering.zinger.seller.ui.menuItem

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.CategoryItemListModel
import com.food.ordering.zinger.seller.data.model.ItemModel
import com.food.ordering.zinger.seller.data.model.ShopModel
import com.food.ordering.zinger.seller.databinding.*
import com.food.ordering.zinger.seller.ui.home.HomeActivity
import com.food.ordering.zinger.seller.ui.menu.CategoryAdapter
import com.food.ordering.zinger.seller.ui.menu.MenuViewModel
import com.food.ordering.zinger.seller.ui.otp.OTPActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

// todo seller based restriction must be implemented

class MenuItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuItemBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var menuAdapter: MenuItemAdapter
    private var menuItemList: ArrayList<ItemModel> = ArrayList()
    private var category: String = ""
    private val viewModel: MenuViewModel by viewModel()
    private var changedItemImageUrl = ""
    private lateinit var dialogBinding: BottomSheetAddEditMenuItemBinding
    private var mStorageRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getArgs()
        initView()
        setListener()
        setObserver()
        setupRecyclerView()
    }

    private fun getArgs() {

        val categoryItemList = Gson().fromJson(
            intent.getStringExtra(AppConstants.CATEGORY_ITEM_DETAIL),
            CategoryItemListModel::class.java
        )
        menuItemList = categoryItemList.itemModelList
        category = categoryItemList.category
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu_item)
        progressDialog = ProgressDialog(this)
        binding.textCategoryName.text = category
        mStorageRef = FirebaseStorage.getInstance().getReference()
        binding.switchDelivery.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchNotSelected))
    }

    private fun setListener() {

        binding.imageClose.setOnClickListener { onBackPressed() }

        binding.switchDelivery.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
                binding.switchDelivery.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchSelected))
            else
                binding.switchDelivery.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchNotSelected))


            binding.buttonSaveChanges.visibility = View.VISIBLE

            for (menu in menuItemList) {
                menu.isAvailable = if (isChecked) 1 else 0
            }

            menuAdapter.notifyDataSetChanged()
        }

        binding.textAddItem.setOnClickListener {
            showCategoryAdditionBottomSheet()
        }

        binding.buttonSaveChanges.setOnClickListener {
            binding.buttonSaveChanges.visibility = View.GONE
            // todo update a list of items
        }
    }

    private fun setObserver() {

        viewModel.menuRequestResponse.observe(this, androidx.lifecycle.Observer { resource ->
            println("resource: " + resource)
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()

                        resource.data?.let {
                            menuItemList.clear()
                            it.data?.filter { it.category == category }
                                ?.let { it1 -> menuItemList.addAll(it1) }
                            menuAdapter.notifyDataSetChanged()
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

        viewModel.performUploadImageStatus.observe(this, androidx.lifecycle.Observer { resource ->
            println("resource: " + resource)
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()

                        resource.data?.let {

                            changedItemImageUrl = resource.data
                            // todo change this later
                            changedItemImageUrl = changedItemImageUrl.substring(0, 45)
                            dialogBinding.imageItem.visibility = View.VISIBLE
                            Picasso.get().load(resource.data)
                                .placeholder(R.drawable.ic_shop)
                                .into(dialogBinding.imageItem)
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

        viewModel.addItemResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        preferencesHelper.currentShop?.let { viewModel.getMenu(it) }
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

        viewModel.updateItemResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()

                        resource.data?.let {
                            preferencesHelper.currentShop?.let { viewModel.getMenu(it) }
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

        viewModel.deleteItemResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        preferencesHelper.currentShop?.let { viewModel.getMenu(it) }
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


    }

    var deleteItemRequest = -1
    private fun setupRecyclerView() {

        menuAdapter =
            MenuItemAdapter(this, menuItemList, object : MenuItemAdapter.OnItemClickListener {
                override fun onEditClick(itemModel: ItemModel?, position: Int) {
                    showCategoryAdditionBottomSheet(itemModel)
                }

                override fun onDeleteClick(itemModel: ItemModel?, position: Int) {

                    MaterialAlertDialogBuilder(this@MenuItemActivity)
                        .setTitle("Delete Item")
                        .setMessage("Do you want to delete " + itemModel?.name + " ?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            itemModel?.id?.let {
                                deleteItemRequest = it
                                viewModel.deleteItem(it)
                            }
                            menuAdapter.notifyDataSetChanged()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

                override fun onSwitchChange() {
                    binding.buttonSaveChanges.visibility=View.VISIBLE
                }
            })

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerMenuItems.layoutManager = layoutManager
        binding.recyclerMenuItems.adapter = menuAdapter
        menuAdapter.notifyDataSetChanged()
    }

    var itemListAddRequest = ArrayList<ItemModel>()
    var itemUpdateRequest: ItemModel? = null
    private fun showCategoryAdditionBottomSheet(item: ItemModel? = null) {

        dialogBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.bottom_sheet_add_edit_menu_item,
                null,
                false
            )

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        if (item != null) {
            dialogBinding.editItemName.setText(item?.name)
            dialogBinding.editItemPrice.setText(item?.price?.toInt().toString())
            dialogBinding.switchAvailability.isChecked = if (item?.isAvailable == 1) true else false
            dialogBinding.switchVeg.isChecked = if (item?.isVeg == 1) true else false
            dialogBinding.imageItem.visibility = View.VISIBLE
            item?.photoUrl.let {
                Picasso.get().load(item?.photoUrl).placeholder(R.drawable.ic_food)
                    .into(dialogBinding.imageItem)
            }
            dialogBinding.buttonAddEdit.text = "UPDATE"
            dialogBinding.textChangeImage.text = "UPDATE IMAGE"


            if (dialogBinding.switchAvailability.isChecked)
                dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchSelected
                    )
                )
            else
                dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(applicationContext, R.color.switchNotSelected)
                )

            if(dialogBinding.switchVeg.isChecked)
                dialogBinding.switchVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(applicationContext, R.color.nonVegetarianSelect)
                )
            else
                dialogBinding.switchVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchNotSelected
                    )
                )

        } else {
            dialogBinding.imageItem.visibility = View.GONE
            dialogBinding.textChangeImage.text = "ADD IMAGE"
        }

        dialogBinding.switchAvailability.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                item?.isAvailable = 1
                dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(applicationContext, R.color.switchSelected)
                )
            } else {
                item?.isAvailable = 0
                dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(applicationContext, R.color.switchNotSelected)
                )
            }
        }

        dialogBinding.switchVeg.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                item?.isVeg = 1
                dialogBinding.switchVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.nonVegetarianSelect
                    )
                )
            } else {
                item?.isVeg = 0
                dialogBinding.switchVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchNotSelected
                    )
                )
            }
        }

        dialogBinding.buttonAddEdit.setOnClickListener { v ->

            var isAvailable = if (dialogBinding.switchAvailability.isChecked) 1 else 0
            var isVeg = if (dialogBinding.switchVeg.isChecked) 1 else 0
            var name = dialogBinding.editItemName.text.toString()
            var price = dialogBinding.editItemPrice.text.toString().toDouble()
            var itemUrl =
                if (changedItemImageUrl.length == 0) item?.photoUrl else changedItemImageUrl

            if (item != null) {

                itemUpdateRequest = ItemModel(
                    category,
                    item.id,
                    isAvailable,
                    isVeg,
                    name,
                    itemUrl!!,
                    price,
                    null
                )
                viewModel.updateItem(itemUpdateRequest!!)
            } else {

                if (changedItemImageUrl.length == 0) {
                    Toast.makeText(this, "Add an image", Toast.LENGTH_SHORT).show()
                } else {
                    var shopModel = ShopModel(id = preferencesHelper.currentShop)
                    var itemInsertRequest = ItemModel(
                        category,
                        null,
                        isAvailable,
                        isVeg,
                        name,
                        changedItemImageUrl,
                        price,
                        shopModel
                    )
                    itemListAddRequest = ArrayList<ItemModel>()
                    itemListAddRequest.add(itemInsertRequest)
                    viewModel.addItem(itemListAddRequest)
                }


            }
            dialog.dismiss()
        }

        dialogBinding.textChangeImage.setOnClickListener { v ->
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .start()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == ImagePicker.REQUEST_CODE) {

                val fileUri = data?.data
                val file: File? = ImagePicker.getFile(data)
                var storageReference: StorageReference? = null

                storageReference =
                    mStorageRef?.child("itemImage/" + preferencesHelper.id + "/" + file?.name + Calendar.getInstance().time)

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
