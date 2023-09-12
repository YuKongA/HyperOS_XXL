package com.yuk.miuiXXL.hooks.modules.packageinstaller

import android.content.pm.ApplicationInfo
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.KotlinXposedHelper.findClassOrNull
import com.yuk.miuiXXL.utils.KotlinXposedHelper.hookBeforeMethod
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean

object AllowUpdateSystemApp : BaseHook() {
    override fun init() {

        if (!getBoolean("packageinstaller_allow_update_system_app", false)) return
        var letter = 'a'
        for (i in 0..25) {
            try {
                val classIfExists = "j2.${letter}".findClassOrNull()
                classIfExists?.let {
                    it.methodFinder().filterByParamCount(1).filterByParamTypes(ApplicationInfo::class.java, Boolean::class.java).first().createHook {
                        returnConstant(false)
                    }
                }
            } catch (t: Throwable) {
                letter++
            }
        }

        try {
            "android.os.SystemProperties".hookBeforeMethod("getBoolean", String::class.java, Boolean::class.java) {
                if (it.args[0] == "persist.sys.allow_sys_app_update") it.result = true
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
    }

}
