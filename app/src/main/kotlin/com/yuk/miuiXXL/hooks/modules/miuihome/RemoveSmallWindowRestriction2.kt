package com.yuk.miuiXXL.hooks.modules.miuihome

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean

object RemoveSmallWindowRestriction2 : BaseHook() {
    override fun init() {

        if (!getBoolean("android_remove_small_window_restriction", false)) return
        val recentsAndFSGestureUtilsClass = loadClass("com.miui.home.launcher.RecentsAndFSGestureUtils")
        recentsAndFSGestureUtilsClass.methodFinder().filterByName("canTaskEnterSmallWindow").first().createHook {
            returnConstant(true)
        }
        recentsAndFSGestureUtilsClass.methodFinder().filterByName("canTaskEnterMiniSmallWindow").first().createHook {
            returnConstant(true)
        }
    }

}
