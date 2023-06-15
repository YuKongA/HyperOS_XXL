package com.yuk.miuiXXL.hooks.modules.updater

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean

object VABUpdate : BaseHook() {
    override fun init() {

        if (!getBoolean("updater_fuck_vab_update", false)) return
        loadClass("miui.util.FeatureParser").methodFinder().filterByName("hasFeature").filterByParamCount(2).first().createHook {
            before {
                if (it.args[0] == "support_ota_validate") {
                    it.result = false
                }
            }
        }
    }

}
