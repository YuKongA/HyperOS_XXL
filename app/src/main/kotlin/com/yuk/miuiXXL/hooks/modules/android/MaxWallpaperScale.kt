package com.yuk.miuiXXL.hooks.modules.android

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getFloat
import com.yuk.miuiXXL.utils.setFloatField

object MaxWallpaperScale : BaseHook() {
    override fun init() {

        val value = getFloat("android_max_wallpaper_scale", 1.2f)
        if (value == 1.2f) return
        val wallpaperControllerClass = loadClass("com.android.server.wm.WallpaperController")
        wallpaperControllerClass.methodFinder().filterByName("zoomOutToScale").filterByParamTypes(Float::class.java).first().createHook {
            before {
                it.thisObject.setFloatField("mMaxWallpaperScale", value)
            }
        }
        wallpaperControllerClass.constructors.createHooks {
            after {
                it.thisObject.setFloatField("mMaxWallpaperScale", value)
            }
        }
    }

}
