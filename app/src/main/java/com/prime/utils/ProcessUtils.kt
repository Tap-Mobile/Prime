package com.prime.utils

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.text.TextUtils
import androidx.annotation.RequiresApi
import timber.log.Timber
import java.util.*

/**
 * Developed by
 * @author Aleksandr Artemov
 * Timber and other dev tools may be unavailable when this class is called
 */
object ProcessUtils {

    fun isMainProcess(context: Context): Boolean {
        val processName: String = getAppProcessName(context)

        return TextUtils.isEmpty(processName) || context.packageName.equals(processName, true)
    }

    private fun isProcess(context: Context, name: String): Boolean {
        val processName = getAppProcessName(context)
        return !TextUtils.isEmpty(processName) && processName.toLowerCase(Locale.US).endsWith(name)
    }

    private fun getAppProcessName(context: Context): String {
        var processName = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            processName = getAppProcessNameP()
        }
        if (TextUtils.isEmpty(processName)) {
            processName = getProcessInvoke(context.applicationContext as Application)
        }
        if (TextUtils.isEmpty(processName)) {
            processName = getAppProcessNameCompatibility(context)
        }

        return processName
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun getAppProcessNameP(): String {
        return Application.getProcessName()
    }

    private fun getProcessInvoke(application: Application): String {
        return try {
            val loadedApkField = application.javaClass.getField("mLoadedApk")
            loadedApkField.isAccessible = true
            val loadedApk = loadedApkField[application]
            val activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
            activityThreadField.isAccessible = true
            val activityThread = activityThreadField[loadedApk]
            val getProcessName = activityThread.javaClass.getDeclaredMethod("getProcessName")
            getProcessName.invoke(activityThread) as String
        } catch (error: Exception) {
            Timber.w(error, "ProcessUtils")
            ""
        }
    }

    private fun getAppProcessNameCompatibility(context: Context): String {
        val pid = Process.myPid()
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = manager.runningAppProcesses

        if (infos != null) {
            for (processInfo in infos) {
                if (processInfo.pid == pid) {
                    return processInfo.processName
                }
            }
        }

        return ""
    }
}