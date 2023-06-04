package com.yuk.miuiXXL.hooks.modules.thememanager

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.setObjectField

object FuckValidateTheme1 : BaseHook() {
    override fun init() {

        if (!getBoolean("thememanager_fuck_validate_theme", false)) return
        try {
            loadClass("com.android.thememanager.detail.theme.model.OnlineResourceDetail").methodFinder().filterByName("toResource").toList().createHooks {
                after {
                    it.thisObject.setObjectField("bought", true)
                }
            }
        } catch (t: Throwable) {
            Log.ex(t)
        }

        try {
            loadClass("com.android.thememanager.basemodule.views.DiscountPriceView").methodFinder().filterByParamCount(2).filterByParamTypes(Int::class.java)
                .filterByParamTypes(Int::class.java).toList().createHooks {
                    before {
                        it.args[1] = 0
                    }
                }
        } catch (t: Throwable) {
            Log.ex(t)
        }
    }

}
