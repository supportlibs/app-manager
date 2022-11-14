package com.lib.app_manager.receivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class UninstallReceiverManager(val context: Context) {

    private lateinit var deleteAppReceiver: DeleteAppReceiver

    fun registerDeleteAppReceiver(callback: () -> Unit) {
        deleteAppReceiver = DeleteAppReceiver(callback)
        IntentFilter().apply {
            for (i in listOf(Intent.ACTION_PACKAGE_REMOVED)) {
                addAction(i)
                priority = 999
                if (i == Intent.ACTION_PACKAGE_REMOVED) addDataScheme("package")
            }
            context.registerReceiver(deleteAppReceiver, this)
        }
    }

    fun unregisterDeleteAppReceiver() {
        context.unregisterReceiver(deleteAppReceiver)
    }

}