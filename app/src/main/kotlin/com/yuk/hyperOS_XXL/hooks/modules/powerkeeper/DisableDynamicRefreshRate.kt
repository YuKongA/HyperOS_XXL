package com.yuk.hyperOS_XXL.hooks.modules.powerkeeper

import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.hookBeforeMethod
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean

object DisableDynamicRefreshRate : BaseHook() {
    override fun init() {

        if (!getBoolean("powerkeeper_disable_dynamic_refresh_rate", false)) return
        "com.miui.powerkeeper.statemachine.DisplayFrameSetting".hookBeforeMethod("isFeatureOn") {
            it.result = false
        }
        "com.miui.powerkeeper.statemachine.DisplayFrameSetting".hookBeforeMethod(
            "setScreenEffect", String::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType
        ) {
            it.result = null
        }
        "com.miui.powerkeeper.statemachine.DisplayFrameSetting".hookBeforeMethod(
            "setScreenEffectInternal", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, String::class.java
        ) {
            it.result = null
        }
    }

}