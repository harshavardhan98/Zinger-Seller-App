package com.food.ordering.zinger.seller.data.retrofit

import com.food.ordering.zinger.seller.data.model.ItemModel
import retrofit2.Retrofit

class ItemRepository(private val retrofit: Retrofit) {

    val rf = retrofit.create(CustomApi::class.java)

    suspend fun getShopMenu(shopId: Int) = rf.getShopMenu(shopId);

    suspend fun addItem(item: List<ItemModel>) = rf.addItem(item)

    suspend fun updateItem(item: ItemModel) = rf.updateItem(item)

    suspend fun deleteItem(itemId: Int) = rf.deleteItem(itemId)

    suspend fun unDeleteItem(itemId: Int) = rf.unDeleteItem(itemId)
}