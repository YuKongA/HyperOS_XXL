package com.yuk.hyperOS_XXL.hooks.modules.miuihome

import android.graphics.RectF
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.callMethod
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.callStaticMethod
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.findClass
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.hookAfterMethod
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getInt

object TaskViewCardSize : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_task_view_card_size_binding", false)) return
        val value = getInt("miuihome_task_view_card_size_vertical", 100).toFloat() / 100f
        val value1 = getInt("miuihome_task_view_card_size_horizontal1", 100).toFloat() / 100f
        val value2 = getInt("miuihome_task_view_card_size_horizontal2", 100).toFloat() / 100f

        "com.miui.home.recents.views.TaskStackViewsAlgorithmVertical".hookAfterMethod(
            "scaleTaskView", RectF::class.java
        ) {
            "com.miui.home.recents.util.Utilities".findClass().callStaticMethod(
                "scaleRectAboutCenter", it.args[0], value
            )
        }

        "com.miui.home.recents.views.TaskStackViewsAlgorithmHorizontal".hookAfterMethod(
            "scaleTaskView", RectF::class.java,
        ) {
            "com.miui.home.recents.util.Utilities".findClass().callStaticMethod(
                "scaleRectAboutCenter", it.args[0], if (it.thisObject.callMethod("isLandscapeVisually") as Boolean) value2 else value1
            )
        }
    }

}
