package com.example.weatherappandroid.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotifyBroadcastReiceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Nhận dc broadcast thì khởi động service
        Log.d("NotiBroadcast", "Broadcast received")
        val serviceIntent = Intent(context, DailyNotificationsService::class.java)
        context?.startService(serviceIntent)
    }
}