package com.food.ordering.zinger.seller.data.retrofit

import com.food.ordering.zinger.seller.data.model.*
import retrofit2.http.*

interface CustomApi  {

    // user repository
    @POST("/user/seller")
    suspend fun login(@Body userModel: UserModel): Response<UserShopListModel>

    @PATCH("/user")
    suspend fun updateProfile(@Body userModel: UserModel): Response<String>

    // seller repository

    @GET("/user/seller/{shopId}")
    suspend fun getSellers(@Path("shopId") shopId: String): Response<List<UserModel>>

    @POST("/user/seller/invite")
    suspend fun inviteSeller(@Body userShop: UserShopModel): Response<String>

    @GET("/user/verify/invite/{shopId}/{phoneNum}")
    suspend fun verifyInvite(@Path("shopId") shopId: Int,@Path("phoneNum") phoneNum: String): Response<UserInviteModel>

    @POST("/user/accept/invite")
    suspend fun acceptInvite(@Body userShop:UserShopModel): Response<String>

    @PATCH("/user/seller/invite")
    suspend fun deleteInvite(@Body userShop:UserShopModel): Response<String>

    @POST("notify/seller/invite")
    suspend fun notifyInvite(@Body userShop: UserShopModel): Response<String>


    // shop repository
    @PATCH("/shop/config")
    suspend fun updateShopConfiguration(@Body shopConfigRequest: ShopConfigRequest): Response<String>


    // Item Repository
    @GET("/menu/shop/{shopId}")
    suspend fun getShopMenu(@Path("shopId") shopId: Int): Response<List<ItemModel>>

    @POST("/menu")
    suspend fun addItem(@Body itemModelList: List<ItemModel>): Response<String>

    @PATCH("/menu")
    suspend fun updateItem(@Body item: ItemModel): Response<String>

    @DELETE("/menu/delete/{itemId}")
    suspend fun deleteItem(@Path("itemId") itemId: Int): Response<String>

    @DELETE("/menu/undelete/{itemId}")
    suspend fun unDeleteItem(@Path("itemId") itemId: Int): Response<String>


    // Order Repository

    @GET("/order/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Int): Response<TransactionModel>

    @GET("/order/seller/{shopId}/{pageNum}/{pageCnt}")
    suspend fun getOrderByPagination(@Path("shopId") shopId: Int,@Path("pageNum") pageNum: Int,@Path("pageCnt") pageCnt: Int): Response<List<OrderItemList>>

    @GET("/order/seller/{shopId}")
    suspend fun getOrderByShopId(@Path("shopId") shopId: Int): Response<List<OrderItemList>>

    @PATCH("/order/status")
    suspend fun updateOrderStatus(@Body order: OrderModel):Response<String>


}