package com.food.ordering.zinger.seller.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.model.OrderItemList
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.databinding.ItemAccountBinding
import com.food.ordering.zinger.seller.databinding.ItemOrderBinding
import com.squareup.picasso.Picasso

class AccountAdapter(
    private val accountList: List<ShopConfigurationModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<AccountAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding: ItemAccountBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_account,
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(accountList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return accountList.size
    }

    class OrderViewHolder(var binding: ItemAccountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(account: ShopConfigurationModel, position: Int, listener: OnItemClickListener) {
            binding.textShopName.text = account.shopModel.name
            Picasso.get().load(account.shopModel.photoUrl).placeholder(R.drawable.ic_shop)
                .into(binding.imageShop)
            binding.radioCurrent.isChecked = account.isSelected
            binding.radioCurrent.setOnCheckedChangeListener { buttonView, isChecked ->
                listener.onItemClick(account, position)
            }
            binding.layoutRoot.setOnClickListener {
                listener.onItemClick(account, position)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: ShopConfigurationModel, position: Int)
    }

}