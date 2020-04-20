package com.food.ordering.zinger.seller.data.retrofit

import com.food.ordering.zinger.seller.data.model.OrderModel
import retrofit2.Retrofit

class OrderRepository(private val retrofit: Retrofit) {

    val retrofitVar =  retrofit.create(CustomApi::class.java)

    suspend fun getOrderById(orderId: Int) = retrofitVar.getOrderById(orderId)

    suspend fun getOrderByShopId(shopId: Int) = retrofitVar.getOrderByShopId(shopId)

    suspend fun getOrderByPagination(shopId: Int,pageNum: Int,pageCnt: Int) = retrofitVar.getOrderByPagination(shopId,pageNum,pageCnt)

    suspend fun updateOrderStatus(order: OrderModel) = retrofitVar.updateOrderStatus(order)
}