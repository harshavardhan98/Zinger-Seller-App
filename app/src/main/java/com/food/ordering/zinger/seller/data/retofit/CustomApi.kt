package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.*
import retrofit2.http.*

interface CustomApi  {

    // user repository
    @POST("/user/seller")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("/user/seller/shopid")
    suspend fun getSellers(): Response<List<UserModel>>

    // shop repository
    @PATCH("/shop/config")
    suspend fun updateShopConfiguration(@Body shopConfigRequest: ShopConfigRequest): Response<String>


    // Item Repository
    @GET("/menu/shop/{shopId}")
    suspend fun getShopMenu(@Path("shopId") shopId: Int): Response<List<ItemModel>>

    @POST("/menu")
    suspend fun addItem(@Body item: ItemModel): Response<String>

    @PATCH("/menu")
    suspend fun updateItem(@Body item: ItemModel): Response<String>

    @DELETE("/menu/delete/{itemId}")
    suspend fun deleteItem(@Path("itemId") itemId: Int): Response<String>

    @DELETE("/menu/undelete/{itemId}")
    suspend fun unDeleteItem(@Path("itemId") itemId: Int): Response<String>


    // Order Repository

    @GET("/order/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: String): Response<TransactionModel>

    @GET("/order/seller/{shopId}/{pageNum}/{pageCnt}")
    suspend fun getOrderByPagination(@Path("shopId") shopId: Int,@Path("pageNum") pageNum: Int,@Path("pageCnt") pageCnt: Int): Response<List<OrderItemList>>

    @GET("/order/seller/{shopId}")
    suspend fun getOrderByShopId(@Path("shopId") shopId: Int): Response<List<OrderItemList>>

    @PATCH("/order/status")
    suspend fun updateOrderStatus(@Body order: OrderModel):Response<String>


    // Notify Repository
    @POST("notify/seller/invite")
    suspend fun notifyRequest(@Body notifyRequest: NotifyRequest): Response<String>

}