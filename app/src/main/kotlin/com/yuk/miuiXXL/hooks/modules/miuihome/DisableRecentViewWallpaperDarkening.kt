package com.yuk.miuiXXL.hooks.modules.miuihome

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean

object DisableRecentViewWallpaperDarkening : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_recentwiew_wallpaper_darkening", false)) return
        val dimlayer = loadClass("com.miui.home.recents.DimLayer")
        dimlayer.methodFinder().filterByName("dim").filterByParamCount(3).first().createHook {
            before {
                it.args[0] = 0.0f
                it.thisObject.objectHelper().setObject("mCurrentAlpha", 0.0f)
            }
        }
    }

}
