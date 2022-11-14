package com.lib.app_manager.model

import android.graphics.drawable.Drawable

data class UninstallAppModel(
    val icon: Drawable,
    val title: String,
    val size: Long,
    val packageName: String
)