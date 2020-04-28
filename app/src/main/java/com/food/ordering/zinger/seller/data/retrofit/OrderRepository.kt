package com.food.ordering.zinger.seller.data.retrofit

import com.food.ordering.zinger.seller.data.model.OrderModel
import retrofit2.Retrofit

class OrderRepository(private val retrofit: Retrofit) {

    val service =  retrofit.create(CustomApi::class.java)

    suspend fun getOrderById(orderId: Int) = service.getOrderById(orderId)

    suspend fun getOrderByShopId(shopId: Int) = service.getOrderByShopId(shopId)

    suspend fun getOrderByPagination(shopId: Int,pageNum: Int,pageCnt: Int) = service.getOrderByPagination(shopId,pageNum,pageCnt)

    suspend fun updateOrderStatus(order: OrderModel) = service.updateOrderStatus(order)
}