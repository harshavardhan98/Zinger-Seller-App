package com.food.ordering.zinger.seller.ui.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.model.CategoryItemListModel
import com.food.ordering.zinger.seller.data.model.OrderItems
import com.food.ordering.zinger.seller.databinding.ItemCategoryBinding
import com.food.ordering.zinger.seller.databinding.ItemOrderProductBinding

class CategoryAdapter(private val context: Context, private val categoryList: List<CategoryItemListModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding: ItemCategoryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_category, parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList.get(position), holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CategoryViewHolder(var binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryItemListModel: CategoryItemListModel, position: Int, listener: OnItemClickListener) {
            binding.textCategoryName.text =categoryItemListModel.category
            binding.textNumberOfItems.text = categoryItemListModel.itemModelList.size.toString()+" item(s)"
            binding.layoutRoot.setOnClickListener{ listener.onItemClick(categoryItemListModel,position) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(categoryItemListModel: CategoryItemListModel?, position: Int)
    }

}