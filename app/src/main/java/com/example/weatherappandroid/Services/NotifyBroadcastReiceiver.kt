package com.example.weatherappandroid.Services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class NotifyBroadcastReiceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        // Nhận dc broadcast thì khởi động service
        Log.d("NotiBroadcast", "Broadcast received")
        val serviceIntent = Intent(context, DailyNotificationsService::class.java)
        context?.startForegroundService(serviceIntent)
    }
}