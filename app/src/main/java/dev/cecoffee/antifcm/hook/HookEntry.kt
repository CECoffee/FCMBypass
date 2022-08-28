package dev.cecoffee.antifcm.hook

import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.Toast
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.XposedBridge
import java.lang.Exception

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        // Your code here.
    }

    override fun onHook() = encase {
        // Your code here.
        packageName
        appInfo
        appContext
        try {
            loadApp("com.bilibili.azurlane") {
                findClass(System::class.java.toString()).hook {
                    injectMember {
                        method {
                            name = "exit"
                            param(Int)
                        }
                        replaceUnit { }
                    }
                }
                findClass(Runtime::class.java.toString()).hook {
                    injectMember {
                        method {
                            name = "exit"
                            param(Int)
                        }
                        replaceUnit { }
                    }
                }
                var context: Context? = null
                findClass(ContextThemeWrapper::class.java.toString()).hook {
                    injectMember {
                        method {
                            name = "attachBaseContext"
                            param()
                        }
                        beforeHook {
                            context = args[0] as Context
                        }
                    }
                }
                try {
                    findClass("com.gsc.base.utils.CommonParamUtils").hook {
                        injectMember {
                            method {
                                name = "generateSign"
                                param(Class.forName("java.utils.Map"))
                                replaceAny {
                                    this.args.forEach { if (it == "sdk_ver"){result = null} }
                                }
                            }
                        }
                    }
                } catch (_: ClassNotFoundException) {
                    Toast.makeText(context, "sdk_ver定位失败", Toast.LENGTH_LONG).show()
                }
                try {
                    findClass("com.gsc.pub.GSCPubCommon").hook {
                        injectMember {
                            method {
                                name = "startHeart"
                                param()
                            }
                            replaceUnit {
                                XposedBridge.log("取消心跳上报")
                                if (context != null) {
                                    Toast.makeText(context, "取消心跳上报", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } catch (_: ClassNotFoundException) {
                    XposedBridge.log("SDK未找到")
                    Toast.makeText(context, "SDK未找到", Toast.LENGTH_LONG).show()
                }
            }
        }catch (_:Exception){}
    }
}