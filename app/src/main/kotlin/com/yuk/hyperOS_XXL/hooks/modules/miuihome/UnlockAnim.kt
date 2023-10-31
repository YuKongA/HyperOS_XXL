package com.yuk.hyperOS_XXL.hooks.modules.miuihome

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean

object UnlockAnim : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_unlock_animation", false)) return
        loadClass("com.miui.home.launcher.compat.UserPresentAnimationCompatV12Phone").methodFinder().filterByName("getSpringAnimator").filterByParamCount(6)
            .first().createHook {
                before {
                    it.args[4] = 0.6f
                    it.args[5] = 0.4f
                }
            }
    }

}
