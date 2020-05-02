package com.food.ordering.zinger.seller.ui.menuItem

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.model.ItemModel
import com.food.ordering.zinger.seller.databinding.ItemMenuBinding
import com.food.ordering.zinger.seller.utils.AppConstants

class MenuItemAdapter(
    private val context: Context,
    private val categoryList: List<ItemModel>,
    private val role:String?,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding: ItemMenuBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_menu,
            parent,
            false
        )
        return MenuItemViewHolder(binding,role)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(categoryList[position], holder.adapterPosition, listener, context)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class MenuItemViewHolder(var binding: ItemMenuBinding,var role: String?) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(menuItem: ItemModel, position: Int, listener: OnItemClickListener, context: Context) {
            binding.textFoodName.text = menuItem.name
            binding.imageDelete.setOnClickListener { listener.onDeleteClick(menuItem, position) }
            binding.imageEdit.setOnClickListener { listener.onEditClick(menuItem, position) }
            binding.textFoodPrice.text = "₹" + menuItem.price.toInt()
            binding.switchItemAvailable.isChecked = menuItem.isAvailable == 1
            if (binding.switchItemAvailable.isChecked)
                binding.switchItemAvailable.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.switchSelected)
                )
            else
                binding.switchItemAvailable.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.switchNotSelected)
                )
            binding.switchItemAvailable.setOnClickListener {
                listener.onSwitchChange(menuItem,position)
            }
            binding.switchItemAvailable.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    menuItem.isAvailable = 1
                    binding.switchItemAvailable.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.switchSelected))
                }
                else{
                    menuItem.isAvailable = 0
                    binding.switchItemAvailable.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.switchNotSelected))
                }
            }
            if (menuItem.isVeg == 1) {
                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_veg))
            } else {
                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_non_veg))
            }
            role?.let {
                if(role.equals(AppConstants.ROLE.SELLER.name)||role.equals(AppConstants.ROLE.DELIVERY.name)){
                    binding.imageEdit.visibility = View.GONE
                    binding.imageEdit.isEnabled = false
                    binding.imageDelete.visibility = View.GONE
                    binding.imageDelete.isEnabled = false
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onEditClick(itemModel: ItemModel?, position: Int)
        fun onDeleteClick(itemModel: ItemModel?, position: Int)
        fun onSwitchChange(itemModel: ItemModel?, position: Int)
    }

}