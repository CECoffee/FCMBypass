package dev.cecoffee.antifcm.hook

import android.widget.Toast
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.type.android.ActivityClass
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import de.robv.android.xposed.XposedBridge
import dev.cecoffee.antifcm.utils.factory.DataHandler
import dev.cecoffee.antifcm.ui.dialog.DataSelectDialog

class HookHandler {
    object CancelHeartBeat : YukiBaseHooker(){
        override fun onHook() {
            "com.gsc.pub.GSCPubCommon".hook {
                injectMember {
                    method {
                        name = "startHeart"
                        param(ActivityClass)
                    }
                    replaceUnit {
                        XposedBridge.log("取消心跳上报")
                        Toast.makeText(appContext, "AntiFCM:取消心跳上报", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    object ForceLogin : YukiBaseHooker(){
        override fun onHook() {
            "com.manjuu.azurlane.GSCSdkCenter".hook {
                injectMember {
                    method {
                        name = "login"
                        returnType = UnitType
                    }
                    afterHook {
                        // TODO 强制登录
                    }
                    replaceUnit {

                    }
                }
            }
        }
    }

    object SelectOutOfTime : YukiBaseHooker(){
        override fun onHook() {
            "com.manjuu.azurlane.GSCSdkCenter".hook {
                injectMember {
                    method {
                        name = "login"
                    }
                    beforeHook {
                        if (DataHandler.dialogDisplayed) {
                            if (!DataHandler.cancelHook) { Toast.makeText(appContext, "超时未选，恢复原版登录", Toast.LENGTH_SHORT).show() }
                            DataSelectDialog().dismissDataSelectDialog()
                            DataHandler.dialogDisplayed = false
                        }
                        DataHandler.cancelHook = true
                    }
                }
            }
        }
    }

    object InjectUserData : YukiBaseHooker() {
        override fun onHook() {
            "com.gsc.pub.GSCPubCommon".hook {
                injectMember { // TODO 向GSCPubCommon的bundle中注入用户数据
                    method { }
                    afterHook { }
                }
            }
        }
    }

    object BypassSDK : YukiBaseHooker(){
        override fun onHook() {
            "com.gsc.base.utils.CommonParamUtils".hook {
                loggerD(msg = "Z: $this")
                injectMember {
                    method {
                        name = "generateSign"
                        param(MapClass)
                        returnType = StringClass
                    }
                    beforeHook {
                        (args[0] as java.util.Map<*, *>).remove("sdk_ver")
                    }
                }
            }

            "okhttp3.FormBody\$Builder".hook {
                injectMember {
                    method {
                        name = "addEncoded"
                        param(StringClass, StringClass)
                    }
                    afterHook {
                        val key = args[0]
                        loggerD(msg = "key: $key")
                        if (key == "sdk_ver") {
                            resultNull()
                        }
                    }
                }
            }
        }
    }
}