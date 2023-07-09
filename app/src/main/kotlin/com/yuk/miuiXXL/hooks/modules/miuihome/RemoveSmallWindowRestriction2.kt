package com.yuk.miuiXXL.hooks.modules.miuihome

import android.util.ArraySet
import com.github.kyuubiran.ezxhelper.ClassUtils.invokeStaticMethodBestMatch
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.ObjectUtils.getObjectOrNullAs
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean

object RemoveSmallWindowRestriction2 : BaseHook() {
    override fun init() {

        if (!getBoolean("android_remove_small_window_restriction", false)) return
        val recentsAndFSGestureUtilsClass = loadClass("com.miui.home.launcher.RecentsAndFSGestureUtils")
        recentsAndFSGestureUtilsClass.methodFinder().filterByName("canTaskEnterSmallWindow").first().createHook {
            returnConstant(true)
        }
        recentsAndFSGestureUtilsClass.methodFinder().filterByName("canTaskEnterMiniSmallWindow").first().createHook {
            before {
                it.result = invokeStaticMethodBestMatch(loadClass("com.miui.home.smallwindow.SmallWindowStateHelper"), "getInstance")!!.objectHelper()
                    .invokeMethodBestMatch("canEnterMiniSmallWindow") as Boolean
            }
        }
        loadClass("com.miui.home.smallwindow.SmallWindowStateHelperUseManager").methodFinder().filterByName("canEnterMiniSmallWindow").first().createHook {
            before {
                it.result = getObjectOrNullAs<ArraySet<*>>(it.thisObject, "mMiniSmallWindowInfoSet")!!.isEmpty()
            }
        }
        loadClass("miui.app.MiuiFreeFormManager").methodFinder().filterByName("getAllFreeFormStackInfosOnDisplay").toList().createHooks {
            before { param ->
                if (Throwable().stackTrace.any {
                        it.className == "android.util.MiuiMultiWindowUtils" && it.methodName == "startSmallFreeform"
                    }) {
                    param.result = null
                }
            }
        }
        loadClass("android.util.MiuiMultiWindowUtils").methodFinder().filterByName("hasSmallFreeform").toList().createHooks {
            before { param ->
                if (Throwable().stackTrace.any {
                        it.className == "android.util.MiuiMultiWindowUtils" && it.methodName == "startSmallFreeform"
                    }) {
                    param.result = false
                }
            }
        }
    }

}
