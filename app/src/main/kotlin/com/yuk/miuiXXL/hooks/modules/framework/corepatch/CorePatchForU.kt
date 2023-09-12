package com.yuk.miuiXXL.hooks.modules.framework.corepatch

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class CorePatchForU : CorePatchForT() {
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        super.handleLoadPackage(loadPackageParam)
        findAndHookMethod(
            "com.android.server.pm.PackageManagerServiceUtils",
            loadPackageParam.classLoader,
            "checkDowngrade",
            "com.android.server.pm.pkg.AndroidPackage",
            "android.content.pm.PackageInfoLite",
            ReturnConstant(prefs(), "downgrade", null)
        )
        if (prefs().getBoolean("digestCreak", true) && prefs().getBoolean("UsePreSig", false)) {
            findAndHookMethod("com.android.server.pm.InstallPackageHelper",
                loadPackageParam.classLoader,
                "doesSignatureMatchForPermissions",
                String::class.java,
                "com.android.server.pm.parsing.pkg.ParsedPackage",
                Int::class.javaPrimitiveType,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        // If we decide to crack this then at least make sure they are same apks, avoid another one that tries to impersonate.
                        if (param.result == false) {
                            val pPname = XposedHelpers.callMethod(param.args[1], "getPackageName") as String
                            if (pPname.contentEquals(param.args[0] as String)) {
                                param.result = true
                            }
                        }
                    }
                })
        }
        findAndHookMethod("com.android.server.pm.ScanPackageUtils",
            loadPackageParam.classLoader,
            "assertMinSignatureSchemeIsValid",
            "com.android.server.pm.pkg.AndroidPackage",
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (prefs().getBoolean("authcreak", true)) {
                        param.result = null
                    }
                }
            })
    }
}