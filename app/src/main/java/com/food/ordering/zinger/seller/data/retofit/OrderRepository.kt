package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.OrderModel
import retrofit2.Retrofit

class OrderRepository(private val retrofit: Retrofit) {

    suspend fun getOrderById(orderId: Int) = retrofit.create(CustomApi::class.java).getOrderById(orderId)

    suspend fun getOrderByShopId(shopId: Int) = retrofit.create(CustomApi::class.java).getOrderByShopId(shopId)

    suspend fun getOrderByPagination(shopId: Int,pageNum: Int,pageCnt: Int) = retrofit.create(CustomApi::class.java).getOrderByPagination(shopId,pageNum,pageCnt)

    suspend fun updateOrderStatus(order: OrderModel) = retrofit.create(CustomApi::class.java).updateOrderStatus(order)


}