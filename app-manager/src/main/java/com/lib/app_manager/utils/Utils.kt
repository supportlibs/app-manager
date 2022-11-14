package com.lib.app_manager.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.lib.app_manager.model.AppModel

object Utils {

    fun getRecentAppsList(context: Context): List<ApplicationInfo> {
        val pm = context.packageManager

        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { it.packageName != context.packageName && isUserApp(it) }
    }

    fun convertToAppModel(it: ApplicationInfo, packageManager: PackageManager): AppModel {
        val appIcon = it.loadIcon(packageManager)
        val appName = it.loadLabel(packageManager)
        return AppModel(
                appName.toString(), appIcon
        )
    }

    fun isUserApp(ai: ApplicationInfo): Boolean {
        val mask = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        return ai.flags and mask == 0
    }

}
