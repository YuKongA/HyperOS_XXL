package com.yuk.miuiXXL.hooks.modules.securitycenter

import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.utils.getBoolean
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Method

class RemoveMacroBlacklist : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!getBoolean("securitycenter_remove_macro_blacklist", false)) return
        System.loadLibrary("dexkit")
        DexKitBridge.create(lpparam.appInfo.sourceDir)?.use { bridge ->

            val classMap = mapOf(
                "Macro" to setOf("pref_gb_unsupport_macro_apps", "gb_game_gunsight", "com.tencent.tmgp.sgame"),
                "Macro1" to setOf("key_macro_toast", "content://com.xiaomi.macro.MacroStatusProvider/game_macro_change"),
            )

            val methodMap = mapOf(
                "Macro2" to setOf("pref_gb_unsupport_macro_apps"),
            )

            val resultClassMap = bridge.batchFindClassesUsingStrings {
                queryMap(classMap)
            }

            val resultMethodMap = bridge.batchFindMethodsUsingStrings {
                queryMap(methodMap)
            }

            val macro = resultClassMap["Macro"]!!
            assert(macro.size == 1)
            val macroDescriptor = macro.first()
            val macroClass: Class<*> = macroDescriptor.getClassInstance(lpparam.classLoader)
            macroClass.methodFinder().filterByReturnType(Boolean::class.java).filterByParamCount(1).first().createHook {
                before {
                    it.result = false
                }
            }

            val macro1 = resultClassMap["Macro1"]!!
            assert(macro1.size == 1)
            val macro1Descriptor = macro1.first()
            val macro1Class: Class<*> = macro1Descriptor.getClassInstance(lpparam.classLoader)
            macro1Class.methodFinder().filterByReturnType(Boolean::class.java).filterByParamCount(2).first().createHook {
                before {
                    it.result = true
                }
            }

            val macro2 = resultMethodMap["Macro2"]!!
            assert(macro2.isNotEmpty())
            var macro2Descriptor = macro2[0]
            var macroMethod: Method = macro2Descriptor.getMethodInstance(lpparam.classLoader)
            if (macroMethod.returnType != ArrayList::class.java) {
                macro2Descriptor = macro2[1]
                macroMethod = macro2Descriptor.getMethodInstance(lpparam.classLoader)
            }
            macroMethod.createHook {
                before {
                    it.result = ArrayList<String>()
                }
            }

        }
    }

}
