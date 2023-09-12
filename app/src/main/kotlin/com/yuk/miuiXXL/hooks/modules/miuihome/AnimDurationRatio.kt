package com.yuk.miuiXXL.hooks.modules.miuihome

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean
import com.yuk.miuiXXL.utils.XSharedPreferences.getInt

object AnimDurationRatio : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_anim_ratio_binding", false)) return
        val value1 = getInt("miuihome_anim_ratio", 100).toFloat() / 100f
        val value2 = getInt("miuihome_anim_ratio_recent", 100).toFloat() / 100f
        val rectFSpringAnimClass = loadClass("com.miui.home.recents.util.RectFSpringAnim")
        val deviceLevelUtilsClass = loadClass("com.miui.home.launcher.common.DeviceLevelUtils")
        rectFSpringAnimClass.methodFinder().filterByName("getModifyResponse").first().createHook {
            before {
                it.result = it.args[0] as Float * value1
            }
        }
        deviceLevelUtilsClass.methodFinder().filterByName("getDeviceLevelTransitionAnimRatio").first().createHook {
            returnConstant(value2)
        }
    }

}
