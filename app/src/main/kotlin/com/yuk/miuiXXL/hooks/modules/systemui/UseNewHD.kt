package com.yuk.miuiXXL.hooks.modules.systemui

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean

object UseNewHD : BaseHook() {
    override fun init() {
        if (!getBoolean("systemui_use_new_hd", false)) return
        runCatching {
            loadClass("com.android.systemui.statusbar.policy.HDController").methodFinder().filterByName("isVisible").first().createHook {
                returnConstant(true)
            }
        }

    }
}