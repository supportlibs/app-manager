package com.lib.app_manager.providers

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.IPackageStatsObserver
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.os.Build
import com.lib.app_manager.model.AppModel
import com.lib.app_manager.model.UninstallAppModel
import com.lib.app_manager.utils.Utils
import kotlinx.coroutines.delay
import java.io.File
import java.lang.reflect.Method
import java.text.StringCharacterIterator

object AppsProvider {

    private var recentApps: List<AppModel>? = null
    private const val APPS_LIST_SIZE = 10

    fun getRecentApps(context: Context): List<AppModel> {
        if (recentApps == null) {
            var apps = Utils.getRecentAppsList(context)
            if (apps.size > APPS_LIST_SIZE) {
                apps = apps.slice(0 until APPS_LIST_SIZE)
            }
            recentApps = apps.map { Utils.convertToAppModel(it, context.packageManager) }
        }
        return recentApps!!
    }

    suspend fun getAppsToUninstall(context: Context): List<UninstallAppModel> =
        Utils.getRecentAppsList(context)
            .map { convertToUninstallAppModel(context, it, context.packageManager) }
            .sortedBy { it.size }
            .reversed()


    private suspend fun convertToUninstallAppModel(
        context: Context,
        app: ApplicationInfo,
        packageManager: PackageManager
    ): UninstallAppModel {
        val appIcon = app.loadIcon(packageManager)
        val appName = app.loadLabel(packageManager)
        val packageName = app.packageName

        return UninstallAppModel(
            appIcon,
            appName.toString(),
            getPackageSizeInfoString(context, packageManager, packageName),
            packageName
        )
    }

    private suspend fun getPackageSizeInfoString(
        context: Context, packageManager: PackageManager, packageName: String
    ): Long {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val storageStatsManager: StorageStatsManager =
                context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager

            try {
                val applicationInfo: ApplicationInfo =
                    context.packageManager.getApplicationInfo(packageName, 0)

                storageStatsManager.queryStatsForUid(
                    applicationInfo.storageUuid,
                    context.packageManager.getApplicationInfo(packageName, 0).uid
                ).apply {
                    return (cacheBytes + dataBytes + appBytes)
                }
            } catch (e: Exception) {
                return getFakePackageSizeInfoString(packageManager, packageName)
            }
        } else {
            return try {
                getPackageSizeInfoBelow26Sdk(packageManager, packageName)
            } catch (e: Exception) {
                getFakePackageSizeInfoString(packageManager, packageName)
            }
        }
    }

    private fun getFakePackageSizeInfoString(
        packageManager: PackageManager,
        packageName: String
    ): Long =
        File(
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            ).publicSourceDir
        ).length()


    fun humanReadableByteCountSI(bytes: Long): String {
        var mBytes = bytes

        if (-1000 < mBytes && mBytes < 1000) {
            return "$mBytes B"
        }
        val ci = StringCharacterIterator("kMGTPE")
        while (mBytes <= -999950 || mBytes >= 999950) {
            mBytes /= 1000
            ci.next()
        }
        return String.format("%.1f %cB", mBytes / 1000.0, ci.current())
    }

    private suspend fun getPackageSizeInfoBelow26Sdk(
        packageManager: PackageManager,
        packageName: String
    ): Long {
        var totalSize = 0L
        val getPackageSizeInfo: Method
        try {
            getPackageSizeInfo = packageManager.javaClass.getMethod(
                "getPackageSizeInfo", String::class.java,
                IPackageStatsObserver::class.java
            )

            getPackageSizeInfo.invoke(
                packageManager, packageName,
                object : IPackageStatsObserver.Stub() {
                    override fun onGetStatsCompleted(pStats: PackageStats, succeeded: Boolean) {
                        totalSize = pStats.codeSize + pStats.dataSize + pStats.codeSize
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        delay(150)
        return totalSize
    }
}