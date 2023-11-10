package com.yuk.hyperOS_XXL.hooks.modules.miuihome

import android.view.Gravity
import android.widget.FrameLayout
import cn.fkj233.ui.activity.dp2px
import com.github.kyuubiran.ezxhelper.EzXHelper.appContext
import com.yuk.hyperOS_XXL.blur.BlurView
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.findClass
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.getObjectField
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.hookAfterMethod
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean

object DockBlur : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_add_dock_blur", false)) return

        val launcherClass = "com.miui.home.launcher.Launcher".findClass()

        // DockBlur, ONLY FOR XIAOMI14 WITH HYPER OS
        // 差解锁动画跟随
        launcherClass.hookAfterMethod("setupViews") {
            val mHotSeats = it.thisObject.getObjectField("mHotSeats") as FrameLayout
            val mDockBlurParent = FrameLayout(appContext)
            val mDockBlur = BlurView(appContext, 30)
            val mDockHeight = dp2px(mDockBlurParent.context, 94f)
            val mDockMargin = dp2px(mDockBlurParent.context, 10f)
            val mDockBottomMargin = dp2px(mDockBlurParent.context, 14f)
            mDockBlurParent.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mDockHeight).also { layoutParams ->
                layoutParams.gravity = Gravity.BOTTOM
                layoutParams.setMargins(mDockMargin, 0, mDockMargin, mDockBottomMargin)
            }
            mDockBlur.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            mHotSeats.addView(mDockBlurParent, 0)
            mDockBlurParent.addView(mDockBlur, 0)
        }
    }
}