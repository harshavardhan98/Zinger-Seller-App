package com.food.ordering.zinger.seller.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.food.ordering.zinger.seller.R
import com.food.ordering.zinger.seller.data.local.PreferencesHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

class ZingerFirebaseMessagingService : FirebaseMessagingService() {


    private val preferencesHelper: PreferencesHelper by inject()


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage?.from}")
        Log.d("FCM", "Content: ${remoteMessage?.data}")
        createNotificationChannel()
        remoteMessage.data.let {
            // todo pass orderId as id
            sendNotification(123,"test",remoteMessage.data.toString())
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

    private fun sendNotification(id: Int,title: String?, message: String?){
        val builder = NotificationCompat.Builder(applicationContext, "7698")
            .setSmallIcon(R.drawable.ic_zinger_notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(id, builder.build())
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM","FCM Token: "+token)
        preferencesHelper.fcmToken = token
        preferencesHelper.isFCMTokenUpdated = false

    }
}