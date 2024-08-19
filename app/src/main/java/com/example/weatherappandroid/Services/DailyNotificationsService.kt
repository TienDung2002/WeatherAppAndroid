package com.example.weatherappandroid.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherappandroid.R


class DailyNotificationsService : Service() {
    // Gọi khi service khởi tạo
    override fun onCreate() {
        Log.d("Service_created", "Service created")
        super.onCreate()
    }

    // Gọi đến bằng startService() từ acti khác
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        Log.d("Service_started", "Service started")
        return START_NOT_STICKY
    }

    // gọi đến khi thành phần khác gọi service bằng câu lệnh bindService()
    override fun onBind(intent: Intent?): IBinder? = null


    // Gọi khi service bị hủy
    override fun onDestroy() {
        Log.d("Service_destroyed", "Service destroyed")
        super.onDestroy()
    }

    private fun showNotification() {
        Log.d("NotiSerCalled", "showNotification called")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_notification_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Daily Notification")  // Tên thành phố here
            .setContentText("This is your daily notification.")     // Thông tin thời tiết
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

}