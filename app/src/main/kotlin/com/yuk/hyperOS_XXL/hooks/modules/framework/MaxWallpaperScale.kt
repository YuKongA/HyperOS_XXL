package com.yuk.hyperOS_XXL.hooks.modules.framework

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getFloat

object MaxWallpaperScale : BaseHook() {
    override fun init() {

        val value = getFloat("android_max_wallpaper_scale", 1.2f)
        if (value == 1.2f) return
        val wallpaperControllerClass = loadClass("com.android.server.wm.WallpaperController")
        wallpaperControllerClass.methodFinder().filterByName("zoomOutToScale").filterByParamTypes(Float::class.java).first().createHook {
            before {
                it.thisObject.objectHelper().setObject("mMaxWallpaperScale", value)
            }
        }
        wallpaperControllerClass.declaredConstructors.createHooks {
            after {
                it.thisObject.objectHelper().setObject("mMaxWallpaperScale", value)
            }
        }
    }

}
