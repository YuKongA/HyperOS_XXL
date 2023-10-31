package com.yuk.hyperOS_XXL.hooks.modules.framework

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean

object RemoveSmallWindowRestriction1 : BaseHook() {
    override fun init() {

        if (!getBoolean("android_remove_small_window_restriction", false)) return
        val taskClass = loadClass("com.android.server.wm.Task")
        val miuiMultiWindowAdapterClass = loadClass("android.util.MiuiMultiWindowAdapter")
        val miuiMultiWindowUtilsClass = loadClass("android.util.MiuiMultiWindowUtils")
        val mActivityTaskManagerServiceClass = loadClass("com.android.server.wm.ActivityTaskManagerService")
        runCatching {
            mActivityTaskManagerServiceClass.methodFinder().filterByName("retrieveSettings").toList().createHooks {
                after {
                    it.thisObject.objectHelper().setObject("mDevEnableNonResizableMultiWindow", true)
                }
            }
        }
        taskClass.methodFinder().filterByName("isResizeable").first().createHook {
            returnConstant(true)
        }
        miuiMultiWindowAdapterClass.methodFinder().filterByName("getFreeformBlackList").first().createHook {
            returnConstant(mutableListOf<String>())
        }
        miuiMultiWindowAdapterClass.methodFinder().filterByName("getFreeformBlackListFromCloud").first().createHook {
            returnConstant(mutableListOf<String>())
        }

        miuiMultiWindowAdapterClass.methodFinder().filterByName("getStartFromFreeformBlackListFromCloud").first().createHook {
            returnConstant(mutableListOf<String>())
        }

        miuiMultiWindowUtilsClass.methodFinder().filterByName("isForceResizeable").first().createHook {
            returnConstant(true)
        }
        miuiMultiWindowUtilsClass.methodFinder().filterByName("supportFreeform").first().createHook {
            returnConstant(true)
        }
    }
}
