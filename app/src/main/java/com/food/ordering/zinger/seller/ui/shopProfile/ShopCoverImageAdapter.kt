package com.food.ordering.zinger.seller.ui.shopProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.databinding.ItemThumbnailBinding
import com.squareup.picasso.Picasso

class ShopCoverImageAdapter(
    private val imageList: List<String>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ShopCoverImageAdapter.ThumbNailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbNailViewHolder {
        val binding: ItemThumbnailBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_thumbnail,
            parent,
            false
        )
        return ThumbNailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThumbNailViewHolder, position: Int) {
        holder.bind(imageList, holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ThumbNailViewHolder(var binding: ItemThumbnailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageList: List<String>, position: Int, listener: OnItemClickListener) {

            Picasso.get().load(imageList.get(position)).placeholder(R.drawable.ic_shop)
                .into(binding.imageCover)

            binding.imageCover.setOnClickListener {
                listener.onItemClick(imageList, position)
            }

            binding.imageClose.setOnClickListener {
                listener.onDeleteClick(imageList, position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: List<String>?, position: Int)
        fun onDeleteClick(item: List<String>?, position: Int)
    }

}