package com.food.ordering.zinger.seller.data.retofit

import com.food.ordering.zinger.seller.data.model.*
import retrofit2.http.*

interface CustomApi  {

    // user repository
    @POST("/user/seller")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("/user/seller/shopid")
    suspend fun getSellers()

    // shop repository
    @PATCH("/shop/config")
    suspend fun updateShopConfiguration(@Body shopConfigurationRequest: ShopConfigurationRequest): ShopConfigurationResponse


    // Item Repository
    @GET("/menu/shop/{shopId}")
    suspend fun getShopMenu(@Path("shopId") shopId: String): MenuResponse

    @POST("/menu")
    suspend fun addItem(@Body item: Item): Response<String>

    @PATCH("/menu")
    suspend fun updateItem(@Body item: Item): Response<String>

    @DELETE("/menu/delete/{itemId}")
    suspend fun deleteItem(@Path("itemId") itemId: Int): Response<String>

    @DELETE("/menu/undelete/{itemId}")
    suspend fun unDeleteItem(@Path("itemId") itemId: Int): Response<String>


    // Order Repository

    @POST("/order/accept/{orderId}")
    suspend fun acceptOrder(@Path("orderId") orderId: Int): Response<String>

    @GET("/order/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Int): OrderResponse

    @GET("/order/seller/{shopId}/{pageNum}/{pageCnt}")
    suspend fun getOrderByPagination(@Path("shopId") shopId: Int,@Path("pageNum") pageNum: Int,@Path("pageCnt") pageCnt: Int): Response<List<OrderModel>>

    @GET("/order/seller/{shopId}")
    suspend fun getOrderByShopId(@Path("shopId") shopId: Int): Response<List<OrderModel>>

    // Notify Repository
    @POST("notify/seller/invite")
    suspend fun notifyRequest(@Body notifyRequest: NotifyRequest): Response<String>

}