package com.yuk.miuiXXL.hooks.modules.guardprovider

import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.luckypray.dexkit.DexKitBridge

class AntiDefraudAppManager : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        System.loadLibrary("dexkit")
        val bridge = DexKitBridge.create(lpparam.appInfo.sourceDir) ?: throw NullPointerException("DexKitBridge.create() failed")
        bridge.findMethod {
            matcher {
                usingStrings = listOf("AntiDefraudAppManager", "https://flash.sec.miui.com/detect/app")
            }
        }.firstOrNull()?.getMethodInstance(lpparam.classLoader)?.createHook {
            replace {
                return@replace null
            }
            bridge.close()
        }
    }

}
