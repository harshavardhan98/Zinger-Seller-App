package com.food.ordering.zinger.seller.ui.menu

import android.app.ProgressDialog
import android.content.Intent
import android.icu.util.ULocale
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.viewmodel.ext.android.viewModel
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.local.Resource
import com.food.ordering.zinger.seller.data.model.CategoryItemListModel
import com.food.ordering.zinger.seller.data.model.ItemModel
import com.food.ordering.zinger.seller.databinding.ActivityMenuBinding
import com.food.ordering.zinger.seller.databinding.BottomSheetAccountSwitchBinding
import com.food.ordering.zinger.seller.databinding.BottomSheetAddCategoryBinding
import com.food.ordering.zinger.seller.ui.menuItem.MenuItemActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import org.koin.android.ext.android.inject

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: MenuViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryAdapter: CategoryAdapter
    private var categoryItemList: ArrayList<CategoryItemListModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_menu)
        getArgs()
        initView()
        setListener()
        setObservers()
        setUpRecyclerView()
    }

    private fun getArgs(){
        //order = Gson().fromJson(intent.getStringExtra(AppConstants.ORDER_DETAIL), OrderItemListModel::class.java)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
    }

    private fun setListener(){

        binding.imageClose.setOnClickListener {
            onBackPressed()
        }

        binding.textAddCategory.setOnClickListener {
            showCategoryAdditionBottomSheet()
        }
    }

    private fun setObservers(){

        viewModel.menuRequestResponse.observe(this, Observer {resource ->
            if(resource!=null){
                when(resource.status){

                    Resource.Status.SUCCESS ->{
                        progressDialog.dismiss()
                        categoryItemList.clear()

                        resource.data?.data?.let {
                            var itemList = it.sortedByDescending { it.category }

                            var categoryItem: CategoryItemListModel? =null

                            for(i in itemList){
                                if(categoryItem!=null){
                                    if(categoryItem.category!=i.category){
                                        categoryItemList.add(categoryItem)
                                        var itemModelList:ArrayList<ItemModel> = ArrayList()
                                        itemModelList.add(i)
                                        categoryItem = CategoryItemListModel(i.category,itemModelList)
                                    }else{
                                        categoryItem.itemModelList.add(i)
                                    }
                                }else{
                                    var itemModelList:ArrayList<ItemModel> = ArrayList()
                                    itemModelList.add(i)
                                    categoryItem = CategoryItemListModel(i.category,itemModelList)
                                }
                            }
                            categoryItem?.let {  categoryItemList.add(categoryItem) }
                            categoryAdapter.notifyDataSetChanged()
                        }

                    }

                    Resource.Status.ERROR ->{
                        progressDialog.dismiss()

                    }

                    Resource.Status.LOADING ->{
                        progressDialog.setMessage("Fetching Menu...")
                        progressDialog.show()

                    }

                    Resource.Status.OFFLINE_ERROR ->{
                        Toast.makeText(this,"Offline erro", Toast.LENGTH_LONG).show()
                    }

                    Resource.Status.EMPTY -> {
                        Toast.makeText(this,"Empty items", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })

    }

    private fun setUpRecyclerView(){
        categoryAdapter = CategoryAdapter(this,categoryItemList,object :CategoryAdapter.OnItemClickListener{
            override fun onItemClick(categoryItemListModel: CategoryItemListModel?, position: Int) {
                val intent = Intent(applicationContext, MenuItemActivity::class.java)
                intent.putExtra(AppConstants.CATEGORY_ITEM_DETAIL,Gson().toJson(categoryItemListModel))
                startActivity(intent)
                finish()
            }
        })
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerCategory.layoutManager = layoutManager
        binding.recyclerCategory.adapter = categoryAdapter
        viewModel.getMenu(preferencesHelper.currentShop)
    }

    private fun showCategoryAdditionBottomSheet(){
        val dialogBinding: BottomSheetAddCategoryBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.bottom_sheet_add_category,
                null,
                false
            )

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.buttonConfirm.setOnClickListener{
            var category = CategoryItemListModel(dialogBinding.editCategory.text.toString().toUpperCase(),ArrayList())
            categoryItemList.add(category)
            categoryAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
    }

}
