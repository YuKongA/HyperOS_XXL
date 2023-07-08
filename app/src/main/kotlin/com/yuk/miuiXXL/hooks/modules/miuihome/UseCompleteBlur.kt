package com.yuk.miuiXXL.hooks.modules.miuihome

import android.app.Activity
import android.view.MotionEvent
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.callStaticMethod
import com.yuk.miuiXXL.utils.findClass
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.getObjectField
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
            navStubViewClass.hookBeforeMethod("onPointerEvent", MotionEvent::class.java) {
                val mLauncher = applicationClass.callStaticMethod("getLauncher") as Activity
                val motionEvent = it.args[0] as MotionEvent
                val action = motionEvent.action
                if (action == 2) Thread.currentThread().priority = 10
                if (it.thisObject.getObjectField("mWindowMode") == 2 && action == 2) {
                    blurUtilsClass.callStaticMethod("fastBlurDirectly", 1.0f, mLauncher.window)
                }
            }
        }
    }

}


