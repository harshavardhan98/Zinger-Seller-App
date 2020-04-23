package com.food.ordering.zinger.seller.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.model.OrderItemListModel
import com.food.ordering.zinger.seller.databinding.ItemOrderBinding
import java.lang.Exception
import java.text.SimpleDateFormat

class OrdersAdapter(private val orderList: List<OrderItemListModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): OrderViewHolder {
        val binding: ItemOrderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_order, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder(var binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderItemListModel, position: Int, listener: OnItemClickListener) {
            //Picasso.get().load(menuItem.photoUrl).into(binding.imageShop)
            binding.textCustomerName.text = order.transactionModel.orderModel.userModel?.name
            try {
                val apiDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                val appDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aaa")
                val date = order.transactionModel.orderModel.date
                val dateString = appDateFormat.format(date)
                binding.textOrderTime.text = dateString
            } catch (e: Exception) {
                e.printStackTrace()
            }
            binding.textOrderPrice.text = "₹ " + order.transactionModel.orderModel.price?.toInt().toString()
            var items = ""
            order.orderItemsList.forEach {
                items += it.quantity.toString() + " X " + it.itemModel.name + "\n"
            }
            binding.textOrderItems.text = items

            if(order.transactionModel.orderModel.deliveryLocation == null)
                binding.textPickUp.text="DELIVERY"
            else
                binding.textPickUp.text="PICKUP"

            binding.layoutRoot.setOnClickListener { listener.onItemClick(order, position) }
            /*binding.buttonTrackRate.setOnClickListener {
                    listener.onUpdateClick(order, position)
            }*/
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: OrderItemListModel?, position: Int)
        fun onUpdateClick(item: OrderItemListModel?, position: Int)
    }

}