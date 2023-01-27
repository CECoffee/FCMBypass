package dev.cecoffee.antifcm.application

import androidx.appcompat.app.AppCompatDelegate
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication

class ModulePrefs : ModuleApplication() {
    init {
    }

    fun setCancelHeartbeatStatus(bool: Boolean) {
        modulePrefs.putBoolean("cancelHeartbeat", bool)
    }

    fun setBypassSdkStatus(bool: Boolean) {
        modulePrefs.putBoolean("bypassSDK", bool)
    }

    override fun onCreate() {
        super.onCreate()
        /**
         * 跟随系统夜间模式
         * Follow system night mode
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // 初始化模块配置
        if (!modulePrefs.name("cancelHeartbeat").isPreferencesAvailable) {
            modulePrefs.putBoolean("cancelHeartbeat", true)
            modulePrefs.putBoolean("bypassSDK", false)
        }
    }
}