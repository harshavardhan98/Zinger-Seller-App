package com.food.ordering.zinger.seller.ui.shopProfile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.model.OrderItemList
import com.food.ordering.zinger.seller.data.model.ShopImageDataModel
import com.food.ordering.zinger.seller.databinding.ItemOrderBinding
import com.food.ordering.zinger.seller.databinding.ItemThumbnailBinding
import com.food.ordering.zinger.seller.ui.home.OrdersAdapter
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat

class ShopCoverImageAdapter(private val imageList: List<ShopImageDataModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ShopCoverImageAdapter.ThumbNailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbNailViewHolder {
        val binding: ItemThumbnailBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_thumbnail, parent, false)
        return ThumbNailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThumbNailViewHolder, position: Int) {
        holder.bind(imageList, holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ThumbNailViewHolder(var binding: ItemThumbnailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageList: List<ShopImageDataModel>, position: Int, listener: OnItemClickListener) {
            var shopImageData = imageList.get(position)
            shopImageData.imageLink?.let {
                Picasso.get().load(shopImageData.imageLink).placeholder(R.drawable.ic_shop)
                    .into(binding.imageCover)
            }
            shopImageData.imageUri?.let {
                binding.imageCover.setImageURI(it)
            }
            binding.imageCover.setOnClickListener{
                listener.onItemClick(imageList,position)
            }
            binding.imageClose.setOnClickListener {
                listener.onDeleteClick(imageList,position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: List<ShopImageDataModel>?, position: Int)
        fun onDeleteClick(item: List<ShopImageDataModel>?, position: Int)
    }

}