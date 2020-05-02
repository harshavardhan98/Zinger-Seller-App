package com.food.ordering .zinger.seller.ui.seller

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.model.UserModel
import com.food.ordering.zinger.seller.databinding.ItemSellerBinding

class SellerAdapter(
    private val userModelList: List<UserModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SellerAdapter.SellerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerViewHolder {
        val binding: ItemSellerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_seller, parent, false)
        return SellerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SellerViewHolder, position: Int) {
        holder.bind(userModelList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return userModelList.size
    }

    class SellerViewHolder(var binding: ItemSellerBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(userModel: UserModel, position: Int, listener: OnItemClickListener) {
            var name = ""
            userModel.name?.let {
                if(!it.toLowerCase().contains("null") && it.isNotEmpty())
                    name= it
            }
            if(name.isNotEmpty())
                binding.textNameNumber.text = userModel.name+" ("+userModel.mobile+")"
            else
                binding.textNameNumber.text = userModel.mobile
            if(userModel.id!=null && userModel.id!=0){
                binding.imagePending.visibility = View.GONE
                binding.textPendingInvitation.visibility = View.GONE
            }
            binding.imageDelete.setOnClickListener { listener.onDeleteClick(userModel,position) }
        }
    }

    interface OnItemClickListener {
        fun onDeleteClick(user: UserModel?, position: Int)
    }

}