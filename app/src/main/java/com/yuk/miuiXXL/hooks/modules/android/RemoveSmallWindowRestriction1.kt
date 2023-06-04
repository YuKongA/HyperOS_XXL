package com.yuk.miuiXXL.hooks.modules.android

import android.content.Context
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean

object RemoveSmallWindowRestriction1 : BaseHook() {
    override fun init() {

        if (!getBoolean("android_remove_small_window_restriction", false)) return
        val taskClass = loadClass("com.android.server.wm.Task")
        val miuiMultiWindowAdapterClass = loadClass("android.util.MiuiMultiWindowAdapter")
        val miuiMultiWindowUtilsClass = loadClass("android.util.MiuiMultiWindowUtils")
        taskClass.methodFinder().filterByName("isResizeable").first().createHook {
            returnConstant(true)
        }
        miuiMultiWindowAdapterClass.methodFinder().filterByName("getFreeformBlackList").first().createHook {
            after {
                it.result = (it.result as MutableList<*>).apply { clear() }
            }
        }
        miuiMultiWindowAdapterClass.methodFinder().filterByName("getFreeformBlackListFromCloud").filterByParamTypes(Context::class.java).first().createHook {
            after {
                it.result = (it.result as MutableList<*>).apply { clear() }
            }
        }
        miuiMultiWindowUtilsClass.methodFinder().filterByName("supportFreeform").first().createHook {
            returnConstant(true)
        }
    }

}
