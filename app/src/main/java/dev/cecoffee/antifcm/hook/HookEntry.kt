package dev.cecoffee.antifcm.hook

import android.widget.Toast
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import dev.cecoffee.antifcm.application.BuildConfig
import dev.cecoffee.antifcm.ui.dialog.DataSelectDialog
import dev.cecoffee.antifcm.utils.factory.DataHandler
import dev.cecoffee.antifcm.utils.factory.DataHandler.Companion.uidDataList

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugLog {
            tag = "AntiFCM"
            isEnable = true
            isRecord = false
            elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
        }
        isDebug = BuildConfig.DEBUG
        isEnableModulePrefsCache = true
        isEnableModuleAppResourcesCache = true
        isEnableHookModuleStatus = true
        isEnableHookSharedPreferences = true
        isEnableDataChannel = true
        isEnableMemberCache = true
    }

    override fun onHook() = encase {
        loadApp("com.bilibili.azurlane") {
            "com.manjuu.azurlane.MainActivity".hook {
                injectMember {
                    beforeHook {
                        //TODO 拦截目标应用程序中的数据库读取方法，获取Cursor对象以读取uid，存入uidDataList
                    }
                    method {
                        name = "onCreate"
                        param(BundleClass)
                        returnType = UnitType
                    }
                    afterHook {
                        Toast.makeText(appContext,"AntiFCM:检测到游戏启动",Toast.LENGTH_SHORT).show()
                        DataSelectDialog().showDataSelectDialog(uidDataList)
                    }
                }
            }

            loadHooker(HookHandler.SelectOutOfTime)

            if (!DataHandler.cancelHook) {
                loadHooker(HookHandler.ForceLogin) //强制登录
                loadHooker(HookHandler.InjectUserData) // 向GSCPubCommon的bundle中注入用户数据
            }

            if (prefs.getBoolean("bypassSDK")) {
                loadHooker(HookHandler.BypassSDK)
            }
            if (prefs.getBoolean("cancelHeartbeat")) {
                loadHooker(HookHandler.CancelHeartBeat)
            }
        }
    }
}