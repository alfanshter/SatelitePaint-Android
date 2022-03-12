package com.satelit.satelitpaint.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.satelit.satelitpaint.MainActivity
import com.satelit.satelitpaint.R
import java.util.*
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt
private const val CHANNEL_ID = "my_channel"

open class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val messageTitle = remoteMessage.notification!!.title
        val messageBody = remoteMessage.notification!!.body

        val click_action = remoteMessage.notification!!.clickAction

        val dataMessage = remoteMessage.data["message"]
        val dataFrom = remoteMessage.data["from_user_id"]
        val latitudeawal = remoteMessage.data["latitudeawal"]
        val longitudeawal = remoteMessage.data["longitudeawal"]
        val latitudetoko = remoteMessage.data["latitudetoko"]
        val longitudetoko = remoteMessage.data["longitudetoko"]
        val alamat = remoteMessage.data["alamat"]
        val namapenjual = remoteMessage.data["namapenjual"]
        val jarak = remoteMessage.data["jarak"]
        val gambar = remoteMessage.data["gambar"]
        val id = remoteMessage.data["id"]
        val harga = remoteMessage.data["harga"]



        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)

        val resultIntent = Intent(click_action)
        resultIntent.putExtra("message", dataMessage)
        resultIntent.putExtra("from_user_id", dataFrom)
        resultIntent.putExtra("latitudeawal", latitudeawal)
        resultIntent.putExtra("longitudeawal", longitudeawal)
        resultIntent.putExtra("latitudetoko", latitudetoko)
        resultIntent.putExtra("longitudetoko", longitudetoko)
        resultIntent.putExtra("alamat", alamat)
        resultIntent.putExtra("namapenjual", namapenjual)
        resultIntent.putExtra("jarak", jarak)
        resultIntent.putExtra("gambar", gambar)
        resultIntent.putExtra("id", id)
        resultIntent.putExtra("harga", harga)


        val resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT
        )

        mBuilder.setContentIntent(resultPendingIntent)


        val mNotificationId = System.currentTimeMillis().toInt()

        val mNotifyMgr =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotifyMgr.notify(mNotificationId, mBuilder.build())

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["message"])
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

}