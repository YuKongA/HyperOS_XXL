package com.yuk.miuiXXL.hooks.modules.systemui

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.getObjectFieldAs
import com.yuk.miuiXXL.utils.setObjectField

object ShowWifiStandard : BaseHook() {
    override fun init() {

        if (!getBoolean("systemui_show_wifi_standard", false)) return
        loadClass("com.android.systemui.statusbar.StatusBarWifiView").methodFinder().filterByName("initViewState").filterByParamCount(1).first().createHook {
            before {
                loadClass("com.android.systemui.statusbar.phone.StatusBarSignalPolicy\$WifiIconState").methodFinder().filterByName("copyTo")
                    .filterByParamCount(1).first().createHook {
                        before {
                            val wifiStandard = it.thisObject.getObjectFieldAs<Int>("wifiStandard")
                            it.thisObject.setObjectField("showWifiStandard", wifiStandard != 0)
                        }
                    }
            }
        }
        loadClass("com.android.systemui.statusbar.StatusBarWifiView").methodFinder().filterByName("updateState").filterByParamCount(1).first().createHook {
            before {
                loadClass("com.android.systemui.statusbar.phone.StatusBarSignalPolicy\$WifiIconState").methodFinder().filterByName("copyTo")
                    .filterByParamCount(1).first().createHook {
                    before {
                        val wifiStandard = it.thisObject.getObjectFieldAs<Int>("wifiStandard")
                        it.thisObject.setObjectField("showWifiStandard", wifiStandard != 0)
                    }
                }
            }
        }
    }

}
