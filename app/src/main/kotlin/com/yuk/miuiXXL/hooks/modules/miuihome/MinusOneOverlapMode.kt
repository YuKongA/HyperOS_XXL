package com.yuk.miuiXXL.hooks.modules.miuihome

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean

object MinusOneOverlapMode : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_minus_one_overlap_mode", false) || !getBoolean("personalassistant_minus_one_blur", false)) return
        val assistantDeviceAdapterClass = loadClass("com.miui.home.launcher.overlay.assistant.AssistantDeviceAdapter")
        assistantDeviceAdapterClass.methodFinder().filterByName("inOverlapMode").first().createHook {
            returnConstant(true)
        }
    }

}
