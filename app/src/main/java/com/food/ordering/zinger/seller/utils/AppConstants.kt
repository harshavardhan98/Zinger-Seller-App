package com.food.ordering.zinger.seller.utils


object AppConstants {
    const val PREFS_AUTH_TOKEN = "auth_token"
    const val PREFS_LOGIN_PREFS = "loginPrefs"
    const val PREFS_CUSTOMER = "customer"
    const val PREFS_CART = "cart"
    const val PREFS_CART_SHOP = "cart_shop"
    const val PREFS_ORDER_DETAIL = "cart_shop"
    const val PREFS_SELLER_ID = "id"
    const val PREFS_SELLER_NAME = "name"
    const val PREFS_SELLER_EMAIL = "email"
    const val PREFS_SELLER_PLACE = "place"
    const val PREFS_CURRENT_SHOP_ID = "current_shop_id"
    const val PREFS_SELLER_MOBILE = "mobile"
    const val PREFS_SELLER_ROLE = "role"
    const val PREFS_SELLER_FCM_TOKEN = "fcm_token"
    const val PREFS_IS_FCM_TOKEN_GENERATED = "fcm_token_generated"
    const val PREFS_IS_FCM_TOPIC_SUBSCRIBED = "fcm_topic_subscribed"
    const val PREFS_ORDER_STATUS_CHANGED = "order_status_changed"
    const val PREFS_ADD_ITEM_REQUEST = "order_status_changed"
    const val PREFS_UPDATE_ITEM_REQUEST = "order_status_changed"
    const val PREFS_DELETE_ITEM_REQUEST = "order_status_changed"


    const val ORDER_STATUS_PENDING = "PENDING"
    const val ORDER_STATUS_TXN_FAILURE = "TXN_FAILURE"
    const val ORDER_STATUS_PLACED = "PLACED"
    const val ORDER_STATUS_CANCELLED_BY_USER = "CANCELLED_BY_USER"
    const val ORDER_STATUS_ACCEPTED = "ACCEPTED"
    const val ORDER_STATUS_CANCELLED_BY_SELLER = "CANCELLED_BY_SELLER"
    const val ORDER_STATUS_READY = "READY"
    const val ORDER_STATUS_OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY"
    const val ORDER_STATUS_COMPLETED = "COMPLETED"
    const val ORDER_STATUS_DELIVERED = "DELIVERED"
    const val ORDER_STATUS_REFUND_INITIATED = "REFUND_INITIATED"
    const val ORDER_STATUS_REFUND_COMPLETED= "REFUND_COMPLETED"
    const val ORDER_DETAIL = "order_detail"

    const val CATEGORY_ITEM_DETAIL = "category_item_detail"
    const val DISPLAY_IMAGE_DETAIL = "display_image_detail"


    // intent constanta
    const val SELLER_INVITE = "seller_invite"
    const val SELLER_SHOP = "seller_shop"
    const val SHOP_ID = "shop_id"
    const val INTENT_ORDER_ID = "order_id"
    const val INTENT_ACCEPT = "accept"
    const val INTENT_DECLINE = "decline"
    const val INTENT_UPDATED_ITEM = "updated_item_list"
    const val INTENT_UPDATED_ITEM_CATEGORY = "updated_item_category"



    //NOTIFICATION TOPICS
    const val NOTIFICATION_TOPIC_GLOBAL = "global"


    // REQUEST CODE
    const val REQUEST_PHONE_CALL = 987
    const val REQUEST_UPDATED_MENU_ITEMS = 321
    const val SUCCESS_UPDATED_MENU_ITEMS = 1

    enum class STATUS{
        PENDING, TXN_FAILURE, PLACED, CANCELLED_BY_USER, ACCEPTED, CANCELLED_BY_SELLER, READY, OUT_FOR_DELIVERY, COMPLETED, DELIVERED, REFUND_INITIATED, REFUND_COMPLETED
    }

    enum class ROLE{
        SHOP_OWNER,SELLER,DELIVERY
    }

    enum class NOTIFICATIONTYPE {
        URL, ORDER_STATUS, NEW_ARRIVAL, NEW_ORDER, ORDER_CANCELLED
    }
}
