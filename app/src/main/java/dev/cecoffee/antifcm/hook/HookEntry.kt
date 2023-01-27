package dev.cecoffee.antifcm.hook

import android.widget.Toast
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.XposedBridge
import dev.cecoffee.antifcm.application.BuildConfig
import dev.cecoffee.antifcm.utils.factory.DataHandler
import dev.cecoffee.antifcm.utils.factory.DataHandler.Companion.uidDataList
import dev.cecoffee.antifcm.ui.dialog.DataSelectDialog
import java.io.File

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
                        val database: File? = appContext?.getDatabasePath("users.db")
                        if (database != null) {
                            try {
                                //TODO 将数据库中的所有uid读取到uidDataList中
                            }catch (e:Exception){
                                XposedBridge.log(e)
                                Toast.makeText(appContext,"数据库连接失败",Toast.LENGTH_SHORT).show()
                            }
                        }else { XposedBridge.log("database为null") }
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