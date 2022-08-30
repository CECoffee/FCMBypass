package dev.cecoffee.antifcm.application

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication

class DefaultApplication : ModuleApplication() {
    init {
    }

    companion object {
        lateinit var shared: SharedPreferences
        fun isCancelHeartbeat(): Boolean {
            return shared.getBoolean("cancelHeartbeat", true)
        }

        fun setCancelHeartbeatStatus(bool: Boolean): Boolean {
            val result = shared.edit().putBoolean("cancelHeartbeat", bool)
            return result.commit()
        }

        fun isBypassSdk(): Boolean {
            return shared.getBoolean("bypassSdk", false)
        }

        fun setBypassSdkStatus(bool: Boolean): Boolean {
            val result = shared.edit().putBoolean("bypassSdk", bool)
            return result.commit()
        }
    }

    override fun onCreate() {
        super.onCreate()
        shared = this.applicationContext.getSharedPreferences("data", MODE_PRIVATE)
        /**
         * 跟随系统夜间模式
         * Follow system night mode
         */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        // Your code here.
        val editor = shared.edit()
        if (!shared.contains("cancelHeartbeat")) {
            editor.putBoolean("cancelHeartbeat", true)
            editor.putBoolean("bypassSdk", false)
            editor.apply()
        }
    }
}