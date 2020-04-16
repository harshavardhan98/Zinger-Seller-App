package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.Item
import retrofit2.Retrofit

class ItemRepository(private val retrofit: Retrofit) {

    suspend fun getShopMenu(shopId: Int) = retrofit.create(CustomApi::class.java).getShopMenu(shopId);

    suspend fun addItem(item: Item) = retrofit.create(CustomApi::class.java).addItem(item)

    suspend fun updateItem(item: Item) = retrofit.create(CustomApi::class.java).updateItem(item)

    suspend fun deleteItem(itemId: Int) = retrofit.create(CustomApi::class.java).deleteItem(itemId)

    suspend fun unDeleteItem(itemId: Int) = retrofit.create(CustomApi::class.java).unDeleteItem(itemId)
}