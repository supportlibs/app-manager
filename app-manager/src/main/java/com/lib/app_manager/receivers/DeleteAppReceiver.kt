package com.lib.app_manager.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DeleteAppReceiver(private val callback: () -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("DeleteAppReceiver", "onReceive: package deleted", )
        callback.invoke()
    }
}