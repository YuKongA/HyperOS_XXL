package com.yuk.miuiXXL.hooks.modules.packageinstaller

import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.findClass
import com.yuk.miuiXXL.utils.findClassOrNull
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.setBooleanField

object RemovePackageInstallerAds : BaseHook() {
    override fun init() {

        if (!getBoolean("packageinstaller_remove_ads", false)) return
        val miuiSettingsCompatClass = "com.android.packageinstaller.compat.MiuiSettingsCompat".findClass()

        try {
            miuiSettingsCompatClass.methodFinder().filterByName("isPersonalizedAdEnabled").filterByReturnType(Boolean::class.java).toList().createHooks {
                before {
                    it.result = false
                }
            }
        } catch (t: Throwable) {
            Log.ex(t)
        }

        var letter = 'a'
        for (i in 0..25) {
            try {
                val classIfExists = "com.miui.packageInstaller.ui.listcomponets.${letter}0".findClassOrNull()
                classIfExists?.let {
                    it.methodFinder().filterByName("a").first().createHook {
                        after { hookParam ->
                            hookParam.thisObject.setBooleanField("l", false)
                        }
                    }
                }
            } catch (t: Throwable) {
                letter++
            }
        }
    }

}
