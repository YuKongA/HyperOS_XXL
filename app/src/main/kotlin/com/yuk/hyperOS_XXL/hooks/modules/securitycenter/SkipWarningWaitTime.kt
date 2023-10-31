package com.yuk.hyperOS_XXL.hooks.modules.securitycenter

import android.widget.TextView
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.hookBeforeMethod
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean

object SkipWarningWaitTime : BaseHook() {
    override fun init() {

        if (!getBoolean("securitycenter_skip_warning_wait_time", false)) return
        TextView::class.java.hookBeforeMethod(
            "setText", CharSequence::class.java, TextView.BufferType::class.java, Boolean::class.java, Int::class.java
        ) {
            if (getBoolean("securitycenter_skip_warning_wait_time", false)) {
                if (it.args.isNotEmpty() && it.args[0]?.toString()?.startsWith("确定(") == true) {
                    it.args[0] = "确定"
                }
            }
        }
        TextView::class.java.hookBeforeMethod(
            "setEnabled", Boolean::class.java
        ) {
            if (getBoolean("securitycenter_skip_warning_wait_time", false)) {
                it.args[0] = true
            }
        }
    }

}
