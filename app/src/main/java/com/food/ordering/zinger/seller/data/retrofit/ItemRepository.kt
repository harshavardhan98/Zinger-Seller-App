package com.food.ordering.zinger.seller.data.retrofit

import com.food.ordering.zinger.seller.data.model.ItemModel
import retrofit2.Retrofit

class ItemRepository(private val retrofit: Retrofit) {

    val service = retrofit.create(CustomApi::class.java)

    suspend fun getShopMenu(shopId: Int) = service.getShopMenu(shopId);

    suspend fun addItem(item: List<ItemModel>) = service.addItem(item)

    suspend fun updateItem(item: ItemModel) = service.updateItem(item)

    suspend fun deleteItem(itemId: Int) = service.deleteItem(itemId)

    suspend fun unDeleteItem(itemId: Int) = service.unDeleteItem(itemId)
}