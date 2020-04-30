package com.food.ordering.zinger.seller.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.food.ordering.zinger.seller.data.model.OrderNotificationPayload
import com.food.ordering.zinger.seller.data.model.ShopConfigurationModel
import com.food.ordering.zinger.seller.ui.home.HomeActivity
import com.food.ordering.zinger.seller.ui.orderdetail.OrderDetailActivity
import com.food.ordering.zinger.seller.ui.webview.WebViewActivity
import com.food.ordering.zinger.seller.utils.AppConstants
import com.food.ordering.zinger.seller.utils.EventBus
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.util.*

class ZingerFirebaseMessagingService : FirebaseMessagingService() {


    private val preferencesHelper: PreferencesHelper by inject()

    @ExperimentalCoroutinesApi
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage?.from}")
        Log.d("FCM", "Content: ${remoteMessage?.data}")
        createNotificationChannel()


        remoteMessage.data.let {

            when (it["type"]) {

                AppConstants.NOTIFICATIONTYPE.URL.name -> {
                    val intent = Intent(this, WebViewActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val title = it["title"]
                    val message = it["message"]
                    val payload = JSONObject(it.get("payload"))
                    if (payload.has("url")) {
                        intent.putExtra(
                            AppConstants.NOTIFICATIONTYPE.URL.name,
                            payload.getString("url").toString()
                        )
                        val pendingIntent: PendingIntent =
                            PendingIntent.getActivity(this, 0, intent, 0)
                        sendNotificationWithPendingIntent(Date().time.toInt(),title, message, pendingIntent)
                    }
                }


                AppConstants.NOTIFICATIONTYPE.NEW_ORDER.name -> {
                    var title = it["title"]
                    var message = it["message"]
                    val payload = Gson().fromJson(it["payload"],OrderNotificationPayload::class.java)
                    println(payload)
                    if(title.isNullOrEmpty()){
                        title = "New Order Received"
                    }
                    if(message.isNullOrEmpty()){
                        message = "OrderId: "+payload.orderId.toString()+"\nItems:\n"
                        for(i in payload.itemList)
                            message+=i+"\n"
                    }

                    val intent = Intent(this, OrderDetailActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    intent.putExtra(AppConstants.INTENT_ORDER_ID,payload.orderId)
                    val acceptIntent = Intent(this, OrderDetailActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    acceptIntent.putExtra(AppConstants.INTENT_ORDER_ID,payload.orderId)
                    acceptIntent.putExtra(AppConstants.INTENT_ACCEPT,true)
                    val declineIntent = Intent(this, OrderDetailActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    declineIntent.putExtra(AppConstants.INTENT_ORDER_ID,payload.orderId)
                    declineIntent.putExtra(AppConstants.INTENT_DECLINE,true)
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, payload.orderId, intent, 0)
                    val acceptPendingIntent: PendingIntent = PendingIntent.getActivity(this, payload.orderId, acceptIntent, 0)
                    val declinePendingIntent: PendingIntent = PendingIntent.getActivity(this, payload.orderId, declineIntent, 0)
                    sendNotificationNewOrder(payload.orderId,title,message,pendingIntent,acceptPendingIntent,declinePendingIntent)
                    EventBus.send(payload)

                }

                AppConstants.NOTIFICATIONTYPE.ORDER_CANCELLED.name -> {
                    var title = it["title"]
                    var message = it["message"]
                    val payload = Gson().fromJson(it["payload"],OrderNotificationPayload::class.java)
                    if(title.isNullOrEmpty()){
                        title = "ORDER CANCELLED"
                    }
                    if(message.isNullOrEmpty()){
                        message = payload.orderId.toString()+"\n"
                        for(i in payload.itemList)
                            message+=i+"\n"
                    }
                    println(payload)
                    val intent = Intent(this, OrderDetailActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    intent.putExtra(AppConstants.INTENT_ORDER_ID,payload.orderId)
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, payload.orderId, intent, 0)
                    sendNotificationWithPendingIntent(payload.orderId,title,message,pendingIntent)
                    EventBus.send(payload)
                }

                AppConstants.NOTIFICATIONTYPE.NEW_ARRIVAL.name -> {
                    val intent = Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    var title = it["title"]
                    var message = it["message"]
                    val payload = JSONObject(it["payload"])
                    var shopName = ""
                    var shopId = ""
                    if(payload.has("shopName")){
                        shopName = payload.getString("shopName").toString()
                    }
                    if(payload.has("shopId")){
                        shopId = payload.getString("shopId").toString()
                    }
                    if(title.isNullOrEmpty()){
                        title+="New Outlet in you place!"
                    }
                    if(message.isNullOrEmpty()){
                        message+= shopName+" has arrived in your place. Try it out!"
                    }
                    intent.putExtra(AppConstants.SHOP_ID,shopId)
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
                    sendNotificationWithPendingIntent(Date().time.toInt(),title,message,pendingIntent)
                }


            }

        }.run {
            remoteMessage.notification?.let {
                sendNotification(Date().time.toInt(),it.title, it.body)
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Orders"
            val descriptionText = "Alerts about order status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("7698", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(id: Int,title: String?, message: String?) {
        val builder = NotificationCompat.Builder(applicationContext, "7698")
            .setSmallIcon(R.drawable.ic_zinger_notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(id, builder.build())
        }
    }

    private fun sendNotificationWithPendingIntent(
        id: Int,
        title: String?,
        message: String?,
        pendingIntent: PendingIntent
    ) {
        val builder = NotificationCompat.Builder(this, "7698")
            .setSmallIcon(R.drawable.ic_zinger_notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(id, builder.build())
        }
    }

    private fun sendNotificationNewOrder(
        id: Int,
        title: String?,
        message: String?,
        pendingIntent: PendingIntent,
        acceptIntent: PendingIntent,
        declineIntent: PendingIntent
    ) {
        val builder = NotificationCompat.Builder(this, "7698")
            .setSmallIcon(R.drawable.ic_zinger_notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_checked,"Accept",acceptIntent)
            .addAction(R.drawable.ic_cancelled,"Decline",declineIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(id, builder.build())
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "FCM Token: " + token)
        preferencesHelper.fcmToken = token
        preferencesHelper.isFCMTokenUpdated = false

    }
}