package com.food.ordering.zinger.seller.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.model.OrderItemListModel
import com.food.ordering.zinger.seller.databinding.ItemOrderBinding
import com.food.ordering.zinger.seller.utils.AppConstants
import java.lang.Exception
import java.text.SimpleDateFormat

class OrdersAdapter(
    private val orderList: List<OrderItemListModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        val binding: ItemOrderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_order,
            parent,
            false
        )
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
            binding.textOrderId.text = order.transactionModel.orderModel.id.toString()
            binding.textCustomerName.text = order.transactionModel.orderModel.userModel?.name
            try {
                val appDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aaa")
                val date = order.transactionModel.orderModel.date
                val dateString = appDateFormat.format(date)
                binding.textOrderTime.text = dateString
            } catch (e: Exception) {
                e.printStackTrace()
            }
            binding.textOrderPrice.text =
                "₹ " + order.transactionModel.orderModel.price?.toInt().toString()
            var items = ""
            order.orderItemsList.forEach {
                items += it.quantity.toString() + " X " + it.itemModel.name + "\n"
            }
            binding.textOrderItems.text = items

            if (order.transactionModel.orderModel.deliveryLocation == null)
                binding.textPickUp.text = "PICKUP"
            else
                binding.textPickUp.text = "DELIVERY"



            when (order.transactionModel.orderModel.orderStatus) {
                AppConstants.STATUS.PLACED.name -> {
                    binding.textUpdateStatus.text = "ACCEPT"
                }

                AppConstants.STATUS.ACCEPTED.name -> {
                    if (order.transactionModel.orderModel.deliveryLocation == null)
                        binding.textUpdateStatus.text = AppConstants.STATUS.READY.name
                    else
                        binding.textUpdateStatus.text = "OUT FOR DELIVERY"
                }

                AppConstants.STATUS.READY.name -> {
                    binding.textCancel.visibility = View.INVISIBLE
                    binding.textCancel.isEnabled = false
                    binding.textUpdateStatus.text = "COMPLETE"
                }

                AppConstants.STATUS.OUT_FOR_DELIVERY.name -> {
                    binding.textCancel.visibility = View.INVISIBLE
                    binding.textCancel.isEnabled = false
                    binding.textUpdateStatus.text = AppConstants.STATUS.DELIVERED.name
                }
            }

            binding.layoutRoot.setOnClickListener { listener.onItemClick(order, position) }
            binding.textUpdateStatus.setOnClickListener { listener.onUpdateClick(order, position) }
            binding.textCancel.setOnClickListener { listener.onCancelClick(order, position) }

        }

    }

    interface OnItemClickListener {
        fun onItemClick(orderItemListModel: OrderItemListModel?, position: Int)
        fun onUpdateClick(orderItemListModel: OrderItemListModel?, position: Int)
        fun onCancelClick(orderItemListModel: OrderItemListModel?, position: Int)
    }

    /* * Update order status
     *  -> Checks if the order status change is valid
     *  -> If new state is READY or OUT_FOR_DELIVERY then secret key is generated and updated
     *  -> If new state is COMPLETED or DELIVERED then secret key sent is checked with secret key in database to validate status change
     *  -> If new state is CANCELLED_BY_SELLER or CANCELLED_BY_USER then refund is initiated
     *  -> The new state is updated in the database
     *
     * * Valid state changes
     * PENDING -> FAILURE ,PLACED
     * PLACED  -> CANCELLED_BY_SELLER,CANCELLED_BY_USER , ACCEPTED
     * CANCELLED_BY_SELLER,CANCELLED_BY_USER -> refund table entry must be added
     * ACCEPTED -> READY, OUT_FOR_DELIVERY , CANCELLED_BY_SELLER -> refund table entry must be added
     * READY -> secret key must be updated in table, COMPLETED
     * OUT_FOR_DELIVERY -> secret key must be updated in table, DELIVERED
     * */
}