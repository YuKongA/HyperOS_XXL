package com.yuk.hyperOS_XXL.hooks.modules.framework.corepatch

import android.os.Build
import com.github.kyuubiran.ezxhelper.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class CorePatchMainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if ("android" == lpparam.packageName && lpparam.processName == "android") {
            Log.d("CorePatch: Current sdk version " + Build.VERSION.SDK_INT)
            when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> CorePatchForU().handleLoadPackage(lpparam)
                else -> Log.ex("CorePatch: Warning: Unsupported Version of Android " + Build.VERSION.SDK_INT)
            }
        }
    }

    override fun initZygote(startupParam: StartupParam) {
        if (startupParam.startsSystemServer) {
            Log.d("CorePatch: Current sdk version " + Build.VERSION.SDK_INT)
            when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> CorePatchForU().initZygote(startupParam)
                else -> Log.ex("CorePatch: Warning: Unsupported Version of Android " + Build.VERSION.SDK_INT)
            }
        }
    }

}
