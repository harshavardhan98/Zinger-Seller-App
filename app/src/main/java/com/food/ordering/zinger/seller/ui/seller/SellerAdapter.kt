package com.food.ordering.zinger.seller.ui.seller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.data.model.UserShopModel
import com.food.ordering.zinger.seller.databinding.ItemCategoryBinding
import com.food.ordering.zinger.seller.databinding.ItemSellerBinding
import com.food.ordering.zinger.seller.ui.menu.CategoryAdapter
import com.food.ordering.zinger.seller.utils.AppConstants

class SellerAdapter(private val context: Context, private val userModelList: List<UserModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<SellerAdapter.SellerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerAdapter.SellerViewHolder {
        val binding: ItemSellerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_seller, parent, false)
        return SellerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SellerAdapter.SellerViewHolder, position: Int) {
        holder.bind(userModelList.get(position), holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return userModelList.size
    }


    class SellerViewHolder(var binding: ItemSellerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userModel: UserModel, position: Int, listener: OnItemClickListener) {
            binding.textNameNumber.text = userModel?.name+"("+userModel.mobile+")"

            if(userModel.id!=null && userModel.id!=0 && userModel.role.equals(AppConstants.ROLE.SELLER.name)){
                binding.imagePending.visibility = View.INVISIBLE
            }

            binding.imageDelete.setOnClickListener { listener.onDeleteClick(userModel,position) }

        }
    }

    interface OnItemClickListener {
        fun onDeleteClick(user: UserModel?, position: Int)
    }

}