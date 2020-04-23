package com.food.ordering.zinger.seller.ui.menuItem

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.model.CategoryItemListModel
import com.food.ordering.zinger.seller.data.model.ItemModel
import com.food.ordering.zinger.seller.databinding.ItemCategoryBinding
import com.food.ordering.zinger.seller.databinding.ItemMenuBinding

class MenuItemAdapter(
    private val context: Context,
    private val categoryList: List<ItemModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding: ItemMenuBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_menu,
            parent,
            false
        )
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(categoryList.get(position), holder.adapterPosition, listener, context)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class MenuItemViewHolder(var binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            menuItem: ItemModel,
            position: Int,
            listener: OnItemClickListener,
            context: Context
        ) {

            binding.textFoodName.text = menuItem.name
            binding.imageDelete.setOnClickListener { listener.onDeleteClick(menuItem, position) }
            binding.imageEdit.setOnClickListener { listener.onEditClick(menuItem, position) }
            binding.textFoodPrice.text = "₹" + menuItem.price.toInt()
            binding.switchItemAvailable.isChecked = if (menuItem.isAvailable == 1) true else false

            if (binding.switchItemAvailable.isChecked)
                binding.switchItemAvailable.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.switchSelected)
                )
            else
                binding.switchItemAvailable.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.switchNotSelected)
                )

            binding.switchItemAvailable.setOnCheckedChangeListener { buttonView, isChecked ->
                listener.onSwitchChange()
                if (isChecked)
                    binding.switchItemAvailable.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.switchSelected))
                else
                    binding.switchItemAvailable.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.switchNotSelected))
            }

            if (menuItem.isVeg == 1) {
                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_veg))
            } else {
                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_non_veg))
            }
        }
    }

    interface OnItemClickListener {
        fun onEditClick(itemModel: ItemModel?, position: Int)
        fun onDeleteClick(itemModel: ItemModel?, position: Int)
        fun onSwitchChange()
    }

}