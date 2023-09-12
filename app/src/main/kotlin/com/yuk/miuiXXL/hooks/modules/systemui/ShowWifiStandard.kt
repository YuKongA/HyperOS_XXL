package com.yuk.miuiXXL.hooks.modules.systemui

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean

object ShowWifiStandard : BaseHook() {
    override fun init() {

        if (!getBoolean("systemui_show_wifi_standard", false)) return
        loadClass("com.android.systemui.statusbar.StatusBarWifiView").methodFinder().filterByName("initViewState").filterByParamCount(1).first().createHook {
            after {
                try {
                    val mWifiIconState = loadClassOrNull("com.android.systemui.statusbar.phone.StatusBarSignalPolicy\$WifiIconState")
                    mWifiIconState?.methodFinder()?.filterByName("copyTo")?.filterByParamCount(1)?.first()?.createHook {
                        before {
                            val wifiStandard = it.thisObject.objectHelper().getObjectOrNullAs<Int>("wifiStandard")
                            if (wifiStandard != null) {
                                it.thisObject.objectHelper().setObject("showWifiStandard", wifiStandard != 0)
                            }
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
        loadClass("com.android.systemui.statusbar.StatusBarWifiView").methodFinder().filterByName("updateState").filterByParamCount(1).first().createHook {
            after {
                try {
                    val mWifiIconState = loadClassOrNull("com.android.systemui.statusbar.phone.StatusBarSignalPolicy\$WifiIconState")
                    mWifiIconState?.methodFinder()?.filterByName("copyTo")?.filterByParamCount(1)?.first()?.createHook {
                        before {
                            val wifiStandard = it.thisObject.objectHelper().getObjectOrNullAs<Int>("wifiStandard")
                            if (wifiStandard != null) {
                                it.thisObject.objectHelper().setObject("showWifiStandard", wifiStandard != 0)
                            }
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

}
