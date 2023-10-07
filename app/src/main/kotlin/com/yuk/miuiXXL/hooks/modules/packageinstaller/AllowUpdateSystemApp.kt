package com.yuk.miuiXXL.hooks.modules.packageinstaller

import android.content.pm.ApplicationInfo
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class AllowUpdateSystemApp : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!getBoolean("packageinstaller_allow_update_system_app", false)) return
        System.loadLibrary("dexkit")
        val bridge = DexKitBridge.create(lpparam.appInfo.sourceDir) ?: throw NullPointerException("DexKitBridge.create() failed")
        bridge.findMethod {
            matcher {
                modifiers = Modifier.PUBLIC or Modifier.STATIC
                paramTypes = listOf("android.content.pm.ApplicationInfo")
                returnType = "boolean"
            }
        }.forEach {
            XposedBridge.log(it.name)
            it.getMethodInstance(lpparam.classLoader).createHook {
                returnConstant(false)
            }
        }
        bridge.close()
    }

}
