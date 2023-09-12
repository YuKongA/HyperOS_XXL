package com.yuk.miuiXXL.hooks.modules.framework

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.atLeastAndroidT
import com.yuk.miuiXXL.utils.getBoolean

object RemoveScreenshotRestriction : BaseHook() {
    override fun init() {

        if (!getBoolean("android_remove_screenshot_restriction", false)) return
        val windowStateClass = loadClass("com.android.server.wm.WindowState")
        val windowSurfaceControllerClass = loadClass("com.android.server.wm.WindowSurfaceController")
        windowStateClass.methodFinder().filterByName("isSecureLocked").first().createHook {
            returnConstant(false)
        }
        if (atLeastAndroidT()) {
            windowSurfaceControllerClass.methodFinder().filterByName("setSecure").first().createHook {
                returnConstant(false)
            }
            windowSurfaceControllerClass.constructors.createHooks {
                before {
                    var flags = it.args[2] as Int
                    val secureFlag = 128
                    flags = flags and secureFlag.inv()
                    it.args[2] = flags
                }
            }
        }
    }

}
