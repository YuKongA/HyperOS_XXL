package com.yuk.hyperOS_XXL.hooks.modules.securitycenter

import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Method

class RemoveMacroBlacklist : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!getBoolean("securitycenter_remove_macro_blacklist", false)) return
        System.loadLibrary("dexkit")
        val bridge = DexKitBridge.create(lpparam.appInfo.sourceDir) ?: throw NullPointerException("DexKitBridge.create() failed")
        bridge.findClass {
            matcher {
                usingStrings = listOf("pref_gb_unsupport_macro_apps", "gb_game_gunsight", "com.tencent.tmgp.sgame")
            }
        }.firstOrNull()?.getInstance(lpparam.classLoader)!!.methodFinder().filterByReturnType(Boolean::class.java).filterByParamCount(1).first().createHook {
            returnConstant(false)
        }
        bridge.findClass {
            matcher {
                usingStrings = listOf("key_macro_toast", "content://com.xiaomi.macro.MacroStatusProvider/game_macro_change")
            }
        }.firstOrNull()?.getInstance(lpparam.classLoader)!!.methodFinder().filterByReturnType(Boolean::class.java).filterByParamCount(2).first().createHook {
            returnConstant(true)
        }
        val macro = bridge.findMethod {
            matcher {
                usingStrings = listOf("pref_gb_unsupport_macro_apps")
            }
        }
        assert(macro.isNotEmpty())
        var macroDescriptor = macro[0]
        var macroMethod: Method = macroDescriptor.getMethodInstance(lpparam.classLoader)
        if (macroMethod.returnType != ArrayList::class.java) {
            macroDescriptor = macro[1]
            macroMethod = macroDescriptor.getMethodInstance(lpparam.classLoader)
        }
        macroMethod.createHook {
            returnConstant(ArrayList<String>())
        }
        bridge.close()
    }

}
