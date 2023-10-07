package com.yuk.miuiXXL.hooks.modules.thememanager

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.FieldFinder.`-Static`.fieldFinder
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import miui.drm.DrmManager
import org.luckypray.dexkit.DexKitBridge
import java.io.File

class FuckValidateTheme2 : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!getBoolean("thememanager_fuck_validate_theme", false)) return
        System.loadLibrary("dexkit")
        System.loadLibrary("dexkit")
        val bridge = DexKitBridge.create(lpparam.appInfo.sourceDir) ?: throw NullPointerException("DexKitBridge.create() failed")
        bridge.findMethod {
            matcher {
                usingStrings = listOf("theme", "ThemeManagerTag", "/system", "check rights isLegal:")
            }
        }.firstOrNull()?.getMethodInstance(lpparam.classLoader)?.createHook {
            returnConstant(DrmManager.DrmResult.DRM_SUCCESS)
        }
        bridge.findMethod {
            matcher {
                usingStrings = listOf("apply failed", "/data/system/theme/large_icons/", "default_large_icon_product_id", "largeicons", "relativePackageList is empty")
            }
        }.firstOrNull()?.getMethodInstance(lpparam.classLoader)?.createHook {
            before {
                val resource = it.thisObject.javaClass.fieldFinder().filterByType(loadClass("com.android.thememanager.basemodule.resource.model.Resource", lpparam.classLoader)).first()
                val productId = it.thisObject.objectHelper().getObjectOrNull(resource.name)!!.objectHelper().invokeMethodBestMatch("getProductId").toString()
                val strPath = "/storage/emulated/0/Android/data/com.android.thememanager/files/MIUI/theme/.data/rights/theme/${productId}-largeicons.mra"
                val file = File(strPath)
                val fileParent = file.parentFile!!
                if (!fileParent.exists()) fileParent.mkdirs()
                file.createNewFile()
            }
        }
        bridge.close()
    }

}
