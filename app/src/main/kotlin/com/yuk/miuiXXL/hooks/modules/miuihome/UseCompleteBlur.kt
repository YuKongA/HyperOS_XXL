package com.yuk.miuiXXL.hooks.modules.miuihome

import android.app.Activity
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.callStaticMethod
import com.yuk.miuiXXL.utils.findClass
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.hookBeforeMethod

object UseCompleteBlur : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_use_complete_blur", false)) return
        val blurUtilsClass = "com.miui.home.launcher.common.BlurUtils".findClass()
        val navStubViewClass = "com.miui.home.recents.NavStubView".findClass()
        val applicationClass = "com.miui.home.launcher.Application".findClass()
        blurUtilsClass.methodFinder().filterByName("getBlurType").first().createHook {
            returnConstant(2)
        }

        if (getBoolean("miuihome_complete_blur_fix", false)) {
            navStubViewClass.hookBeforeMethod("updateDimLayerAlpha", Float::class.java) {
                val mLauncher = applicationClass.callStaticMethod("getLauncher") as Activity
                val value = 1 - it.args[0] as Float
                if (value != 1f) {
                    blurUtilsClass.callStaticMethod("fastBlurDirectly", value, mLauncher.window)
                }
            }
        }
    }

}
