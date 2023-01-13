package dev.cecoffee.antifcm.hook

import android.widget.Toast
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.type.android.ActivityClass
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.MapClass
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import org.ktorm.database.Database
import de.robv.android.xposed.XposedBridge
import dev.cecoffee.antifcm.application.DefaultApplication
import dev.cecoffee.antifcm.utils.factory.UserDB
import org.ktorm.entity.sequenceOf
import java.io.FileInputStream

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    private val application = DefaultApplication
    override fun onInit() = configs {
        // Your code here.
        this.debugTag = "AntiFCM"
        this.isDebug = true
    }

    override fun onHook() = encase {
        // Your code here.
        loadApp("com.bilibili.azurlane") {
            "com.manjuu.azurlane.MainActivity".hook {
                injectMember {
                    beforeHook {
                        val path = appContext.filesDir
                        FileInputStream(path.path + "/databases/users.db").use {
                            try {
                                val database = Database.connect("jdbc:sqlite:${path.path}/databases/users.db", "org.sqlite.JDBC")
                                val userDB = database.sequenceOf(UserDB)
                                Toast.makeText(appContext,userDB.totalRecords,Toast.LENGTH_SHORT).show()
                            }catch (e:Exception){
                                XposedBridge.log(e)
                                Toast.makeText(appContext,"数据库连接失败",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    method {
                        name = "onCreate"
                        param(BundleClass)
                        returnType = UnitType
                    }
                    afterHook {
                        Toast.makeText(appContext,"AntiFCM:检测到游戏启动",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (application.isBypassSdk()) {
                "com.gsc.base.utils.CommonParamUtils".hook {
                    loggerD(msg = "Z: $this")
                    injectMember {
                        method {
                            name = "generateSign"
                            param(MapClass)
                            returnType = StringType
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
                            param(StringType, StringType)
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
            if (application.isCancelHeartbeat()) {
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
    }
}