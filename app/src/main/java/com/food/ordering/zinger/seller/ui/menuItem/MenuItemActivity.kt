package com.food.ordering.zinger.seller.ui.menuItem

import android.R.attr.data
import android.app.Activity
import android.app.ProgressDialog
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
import com.food.ordering.zinger.seller.data.model.CategoryItemListModel
import com.food.ordering.zinger.seller.data.model.ItemModel
import com.food.ordering.zinger.seller.data.model.ShopModel
import com.food.ordering.zinger.seller.databinding.ActivityMenuItemBinding
import com.food.ordering.zinger.seller.databinding.BottomSheetAddEditMenuItemBinding
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
import java.util.Collections.sort
import kotlin.collections.ArrayList


/*
*  1. When no items are available
*       1.1 show empty screen with add items in the middle
*       1.2. Hide the availability switch
*
*  2. when all items are available the availability switch must be set to true else false
*
* */


class MenuItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuItemBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var menuAdapter: MenuItemAdapter
    private var menuItemList: ArrayList<ItemModel> = ArrayList()
    private var category: String = ""
    private val viewModel: MenuItemViewModel by viewModel()
    private var changedItemImageUrl = ""
    private lateinit var dialogBinding: BottomSheetAddEditMenuItemBinding
    private var mStorageRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        sort(menuItemList, object : Comparator<ItemModel> {
            override fun compare(o1: ItemModel, o2: ItemModel): Int {
                return o1.id!! - o2.id!!
            }
        })
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu_item)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        binding.textCategoryName.text = category
        mStorageRef = FirebaseStorage.getInstance().getReference()
        binding.switchDelivery.thumbTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchNotSelected))

        if (menuItemList.isEmpty()) {
            binding.switchDelivery.visibility = View.GONE
            binding.textAddItem.visibility = View.GONE
            binding.textAddFirstItem.visibility = View.VISIBLE
            binding.animationView.visibility = View.VISIBLE
            binding.animationView.loop(true)
            binding.animationView.setAnimation("empty_animation.json")
            binding.animationView.playAnimation()
        } else if (menuItemList.filter { it.isAvailable == 0 }.size == 0) {
            binding.switchDelivery.isChecked = true
            binding.switchDelivery.thumbTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchSelected))
        } else if (menuItemList.filter { it.isAvailable == 1 }.size == 0) {
            binding.switchDelivery.isChecked = false
            binding.switchDelivery.thumbTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchNotSelected))
        }

        preferencesHelper.role?.let {
            if (it.equals(AppConstants.ROLE.SELLER.name) || it.equals(AppConstants.ROLE.DELIVERY.name)) {
                binding.textAddItem.visibility = View.GONE
                binding.textAddItem.isEnabled = false
            }
        }
    }

    private fun setListener() {

        binding.imageClose.setOnClickListener { onBackPressed() }

        binding.switchDelivery.setOnClickListener { buttonView ->

            val isChecked = binding.switchDelivery.isChecked

            if (isChecked) {
                binding.switchDelivery.thumbTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchSelected))
            } else {
                binding.switchDelivery.thumbTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchNotSelected))
            }


            binding.buttonSaveChanges.visibility = View.VISIBLE

            for (menu in menuItemList) {
                menu.isAvailable = if (isChecked) 1 else 0
            }

            menuAdapter.notifyDataSetChanged()
        }

        binding.textAddItem.setOnClickListener {
            if (binding.buttonSaveChanges.visibility == View.VISIBLE) {
                Toast.makeText(
                    applicationContext,
                    "Please save current changes before proceeding",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                showItemAddEditBottomSheet()
            }

        }

        binding.textAddFirstItem.setOnClickListener {
            showItemAddEditBottomSheet()
        }


        binding.buttonSaveChanges.setOnClickListener {
            binding.buttonSaveChanges.visibility = View.GONE
            viewModel.updateItem(menuItemList)
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

                            if (menuItemList.isEmpty()) {
                                binding.switchDelivery.visibility = View.GONE
                                binding.textAddItem.visibility = View.GONE
                                binding.textAddFirstItem.visibility = View.VISIBLE
                                binding.animationView.visibility = View.VISIBLE
                                binding.animationView.loop(true)
                                binding.animationView.setAnimation("empty_animation.json")
                                binding.animationView.playAnimation()
                            } else {
                                binding.switchDelivery.visibility = View.VISIBLE
                                binding.textAddFirstItem.visibility = View.GONE
                                binding.animationView.visibility = View.GONE
                                binding.animationView.cancelAnimation()

                                preferencesHelper.role?.let {
                                    if (it.equals(AppConstants.ROLE.SHOP_OWNER.name)) {
                                        binding.textAddItem.visibility = View.VISIBLE
                                    }
                                }

                                if (menuItemList.filter { it.isAvailable == 0 }.size == 0) {
                                    binding.switchDelivery.isChecked = true
                                    binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.switchSelected
                                        )
                                    )
                                } else {
                                    binding.switchDelivery.isChecked = false
                                    binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.switchNotSelected
                                        )
                                    )
                                }

                            }

                            menuAdapter.notifyDataSetChanged()
                        }
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
                        progressDialog.setMessage("Updating...")
                        progressDialog.show()
                    }
                }
            }
            binding.buttonSaveChanges.visibility = View.GONE
        })

        viewModel.performUploadImageStatus.observe(this, androidx.lifecycle.Observer { resource ->
            println("resource: " + resource)
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()

                        resource.data?.let {

                            changedItemImageUrl = resource.data
                            dialogBinding.imageItem.visibility = View.VISIBLE
                            dialogBinding.textChangeImage.text = "UPDATE IMAGE"
                            Picasso.get().load(resource.data)
                                .placeholder(R.drawable.ic_food)
                                .into(dialogBinding.imageItem)
                        }

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
                        preferencesHelper.currentShop.let { viewModel.getMenu(it) }
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
                        progressDialog.setMessage("Updating...")
                        progressDialog.show()
                    }
                }
                binding.buttonSaveChanges.visibility = View.GONE
            }
        })

        viewModel.updateItemResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        resource.data?.let {
                            if (!preferencesHelper.updateItemRequest.equals("null")) {
                                val listType = object : TypeToken<List<ItemModel?>?>() {}.type
                                updateList(
                                    ArrayList(Gson().fromJson<List<ItemModel>>(preferencesHelper.updateItemRequest, listType))
                                )
                            } else {
                                preferencesHelper.currentShop.let { viewModel.getMenu(it) }
                            }
                        }
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
                        progressDialog.setMessage("Updating...")
                        progressDialog.show()
                    }
                }
                binding.buttonSaveChanges.visibility = View.GONE
            }
        })

        viewModel.deleteItemResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        if (preferencesHelper.deleteItemRequest != -1) {
                            val tempList =
                                menuItemList.filter { it.id != preferencesHelper.deleteItemRequest }
                            menuItemList.clear()
                            menuItemList.addAll(tempList)
                            menuAdapter.notifyDataSetChanged()
                        } else {
                            preferencesHelper.currentShop?.let { viewModel.getMenu(it) }
                        }
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
                        progressDialog.setMessage("Updating...")
                        progressDialog.show()
                    }
                }
                binding.buttonSaveChanges.visibility = View.GONE
            }
        })


    }

    var deleteItemRequest = -1
    private fun setupRecyclerView() {

        menuAdapter =
            MenuItemAdapter(
                this,
                menuItemList,
                preferencesHelper.role,
                object : MenuItemAdapter.OnItemClickListener {
                    override fun onEditClick(itemModel: ItemModel?, position: Int) {
                        if (binding.buttonSaveChanges.visibility == View.VISIBLE) {
                            Toast.makeText(
                                applicationContext,
                                "Please save current changes before proceeding",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            showItemAddEditBottomSheet(itemModel)
                        }
                    }

                    override fun onDeleteClick(itemModel: ItemModel?, position: Int) {

                        if (binding.buttonSaveChanges.visibility == View.VISIBLE) {
                            Toast.makeText(
                                applicationContext,
                                "Please save current changes before proceeding",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
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
                    }

                    override fun onSwitchChange(itemModel: ItemModel?, position: Int) {
                        binding.buttonSaveChanges.visibility = View.VISIBLE
                        //menuItemList.get(position).isAvailable = if(menuItemList.get(position).isAvailable==1) 0 else 1
                    }
                })

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerMenuItems.layoutManager = layoutManager
        binding.recyclerMenuItems.adapter = menuAdapter
        menuAdapter.notifyDataSetChanged()
    }


    var itemListAddRequest = ArrayList<ItemModel>()
    var itemUpdateRequest: ItemModel? = null
    private fun showItemAddEditBottomSheet(item: ItemModel? = null) {

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
            dialogBinding.switchNonVeg.isChecked = if (item?.isVeg == 0) true else false
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

            if (dialogBinding.switchNonVeg.isChecked)
                dialogBinding.switchNonVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(applicationContext, R.color.nonVegetarianSelect)
                )
            else
                dialogBinding.switchNonVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchNotSelected
                    )
                )

        } else {
            dialogBinding.switchAvailability.isChecked = true
            dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.switchSelected
                )
            )
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

        dialogBinding.switchNonVeg.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                item?.isVeg = 0
                dialogBinding.switchNonVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.nonVegetarianSelect
                    )
                )
            } else {
                item?.isVeg = 1
                dialogBinding.switchNonVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchNotSelected
                    )
                )
            }
        }

        dialogBinding.buttonAddEdit.setOnClickListener { v ->

            val isAvailable = if (dialogBinding.switchAvailability.isChecked) 1 else 0
            val isVeg = if (dialogBinding.switchNonVeg.isChecked) 0 else 1
            val name = dialogBinding.editItemName.text.toString()
            var price = -1.0
            if (dialogBinding.editItemPrice.text.toString().length > 0 && dialogBinding.editItemPrice.text.toString()
                    .matches(Regex("\\d+"))
            ) {
                price = dialogBinding.editItemPrice.text.toString().toDouble()
            }
            val itemUrl =
                if (changedItemImageUrl.length == 0) item?.photoUrl else changedItemImageUrl

            if (dialogBinding.editItemPrice.text.toString().length == 0 || price == -1.0) {
                Toast.makeText(
                    this,
                    "Please fill the item name and price details",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (item != null) {

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
                viewModel.updateItem(arrayListOf(itemUpdateRequest!!))
                dialog.dismiss()
            } else {

                if (changedItemImageUrl.length == 0) {
                    Toast.makeText(this, "Add an image", Toast.LENGTH_SHORT).show()
                } else {
                    val shopModel = ShopModel(id = preferencesHelper.currentShop)
                    val itemInsertRequest = ItemModel(
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
                    dialog.dismiss()
                }
            }
        }

        dialogBinding.textChangeImage.setOnClickListener { v ->
            ImagePicker.with(this)
                .galleryOnly()
                .cropSquare()
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


    fun updateList(itemModelList: ArrayList<ItemModel>) {

        itemModelList.sortedBy { item -> item.id }
        sort(menuItemList, object : Comparator<ItemModel> {
            override fun compare(o1: ItemModel, o2: ItemModel): Int {
                return o1.id!! - o2.id!!
            }
        })

        var i = 0
        var k = 0

        while (i < menuItemList.size) {
            if (k < itemModelList.size && menuItemList.get(i).id == itemModelList.get(k).id)
                menuItemList[i] = itemModelList.get(k++)
            i++
        }

        menuAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(AppConstants.INTENT_UPDATED_ITEM, Gson().toJson(menuItemList))
        intent.putExtra(AppConstants.INTENT_UPDATED_ITEM_CATEGORY, category)
        setResult(35, intent)
        // NOTE: do not put on back pressed first, else result code will be 0 instead of 35
        super.onBackPressed()
    }

}
