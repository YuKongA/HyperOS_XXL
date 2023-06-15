package com.yuk.miuiXXL.hooks.modules.miuihome

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.atLeastAndroidT
import com.yuk.miuiXXL.utils.findClass
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.hookBeforeMethod
import com.yuk.miuiXXL.utils.replaceMethod

object SetDeviceLevel : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_highend_device", false)) return
        try {
            loadClass("com.miui.home.launcher.common.CpuLevelUtils").methodFinder().filterByName("getQualcommCpuLevel").filterByParamCount(1)
        } catch (e: Exception) {
            loadClass("miuix.animation.utils.DeviceUtils").methodFinder().filterByName("getQualcommCpuLevel").filterByParamCount(1)
        }.first().createHook {
            returnConstant(2)
        }
        try {
            "com.miui.home.launcher.common.DeviceLevelUtils".hookBeforeMethod("getDeviceLevel") {
                it.result = 2
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "com.miui.home.launcher.DeviceConfig".hookBeforeMethod("isSupportCompleteAnimation") {
                it.result = true
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "com.miui.home.launcher.common.DeviceLevelUtils".hookBeforeMethod("isLowLevelOrLiteDevice") {
                it.result = false
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "com.miui.home.launcher.DeviceConfig".hookBeforeMethod("isMiuiLiteVersion") {
                it.result = false
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "com.miui.home.launcher.util.noword.NoWordSettingHelperKt".hookBeforeMethod("isNoWordAvailable") {
                it.result = true
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "android.os.SystemProperties".hookBeforeMethod(
                "getBoolean", String::class.java, Boolean::class.java
            ) {
                if (it.args[0] == "ro.config.low_ram.threshold_gb") it.result = false
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "android.os.SystemProperties".hookBeforeMethod(
                "getBoolean", String::class.java, Boolean::class.java
            ) {
                if (it.args[0] == "ro.miui.backdrop_sampling_enabled") it.result = true
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "com.miui.home.launcher.common.Utilities".hookBeforeMethod("canLockTaskView") {
                it.result = true
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "com.miui.home.launcher.MIUIWidgetUtil".hookBeforeMethod("isMIUIWidgetSupport") {
                it.result = true
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "com.miui.home.launcher.MiuiHomeLog".findClass().replaceMethod(
                "log", String::class.java, String::class.java
            ) {
                return@replaceMethod null
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            "com.xiaomi.onetrack.OneTrack".hookBeforeMethod("isDisable") {
                it.result = true
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            loadClass("com.miui.home.launcher.common.DeviceLevelUtils").methodFinder().filterByName("needMamlProgressIcon").first().createHook {
                returnConstant(true)
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }
        try {
            loadClass("com.miui.home.launcher.common.DeviceLevelUtils").methodFinder().filterByName("needRemoveDownloadAnimationDevice").first().createHook {
                returnConstant(false)
            }
        } catch (e: Throwable) {
            Log.ex(e)
        }

        try {
            if (atLeastAndroidT()) loadClass("com.miui.home.launcher.graphics.MonochromeUtils").methodFinder().filterByName("isSupportMonochrome").first()
                .createHook {
                    returnConstant(true)
                }
        } catch (e: Throwable) {
            Log.ex(e)
        }
    }

}
