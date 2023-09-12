package com.yuk.miuiXXL.hooks.modules.systemui

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean

object DisableBluetoothRestrict : BaseHook() {
    override fun init() {

        if (!getBoolean("systemui_disable_bluetooth_restrict", false)) return
        loadClass("com.android.settingslib.bluetooth.LocalBluetoothAdapter").methodFinder().filterByName("isSupportBluetoothRestrict").filterByParamCount(1).first().createHook {
            returnConstant(false)
        }
    }

}
