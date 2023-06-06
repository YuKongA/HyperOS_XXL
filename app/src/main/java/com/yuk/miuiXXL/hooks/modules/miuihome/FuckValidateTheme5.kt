package com.yuk.miuiXXL.hooks.modules.miuihome

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean

object FuckValidateTheme5 : BaseHook() {
    override fun init() {

        if (!getBoolean("thememanager_fuck_validate_theme", false)) return
        try {
            loadClass("com.miui.maml.widget.edit.MamlutilKt").methodFinder().filterByName("themeManagerSupportPaidWidget").first().createHook {
                returnConstant(false)
            }
            loadClass("com.miui.home.launcher.gadget.MaMlPendingHostView").methodFinder().filterByName("isCanAutoStartDownload").first().createHook {
                returnConstant(true)
            }
        } catch (t: Throwable) {
            Log.ex(t)
        }
    }
}
