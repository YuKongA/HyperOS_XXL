package com.yuk.miuiXXL.hooks.modules.framework

import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean
import de.robv.android.xposed.XC_MethodHook
import miui.drm.DrmManager
import miui.drm.ThemeReceiver

object FuckValidateTheme3 : BaseHook() {
    override fun init() {

        if (!getBoolean("thememanager_fuck_validate_theme", false)) return
        var hook: List<XC_MethodHook.Unhook>? = null
        try {
            ThemeReceiver::class.java.methodFinder().filterByName("validateTheme").first().createHook {
                before {
                    hook = DrmManager::class.java.methodFinder().filterByName("isLegal").toList().createHooks {
                        returnConstant(DrmManager.DrmResult.DRM_SUCCESS)
                    }
                }
                after {
                    hook?.forEach { it.unhook() }
                }
            }
        } catch (t: Throwable) {
            Log.ex(t)
        }
    }
}
