package com.yuk.hyperOS_XXL.hooks.modules.miuihome

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean

object AlwaysShowStatusBarClock : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_always_show_statusbar_clock", false)) return
        val workspaceClass = loadClass("com.miui.home.launcher.Workspace")
        try {
            workspaceClass.methodFinder().filterByName("isScreenHasClockGadget").first()
        } catch (e: Exception) {
            workspaceClass.methodFinder().filterByName("isScreenHasClockWidget").first()
        } catch (e: Exception) {
            workspaceClass.methodFinder().filterByName("isClockWidget").first()
        }.createHook {
            returnConstant(false)
        }
    }

}
