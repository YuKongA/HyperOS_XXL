package com.yuk.hyperOS_XXL.hooks.modules.miuihome

import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.FrameLayout
import cn.fkj233.ui.activity.dp2px
import com.github.kyuubiran.ezxhelper.EzXHelper.appContext
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.yuk.hyperOS_XXL.blur.BlurView
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.findClass
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.getObjectField
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.hookAfterMethod
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean
import de.robv.android.xposed.XposedHelpers


object DockBlur : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_add_dock_blur", false)) return

        val launcherClass = "com.miui.home.launcher.Launcher".findClass()
        val workspaseClass = "com.miui.home.launcher.Workspace".findClass()
        val applicationClass = "com.miui.home.launcher.Application".findClass()

        // DockBlur WIP, ONLY FOR XIAOMI14 WITH HYPER OS
        // 还差解锁动画跟随
        "com.miui.home.launcher.Launcher".findClass().declaredConstructors.createHooks {
            after {
                var mDockBlurParent = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDockBlurParent")
                var mDockBlur = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDockBlur")
                if (mDockBlurParent != null && mDockBlur != null) return@after
                mDockBlurParent = FrameLayout(appContext)
                mDockBlur = BlurView(appContext, 30)
                XposedHelpers.setAdditionalInstanceField(it.thisObject, "mDockBlur", mDockBlur)
                XposedHelpers.setAdditionalInstanceField(it.thisObject, "mDockBlurParent", mDockBlurParent)
            }
        }
        launcherClass.hookAfterMethod("setupViews") {
            val mHotSeats = it.thisObject.getObjectField("mHotSeats") as FrameLayout
            val mDockBlurParent = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDockBlurParent") as FrameLayout
            val mDockBlur = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDockBlur") as BlurView
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
        launcherClass.hookAfterMethod("showBottomAreaEditPanelIfNeed", View::class.java, Boolean::class.java) {
            val mDockBlurParent = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDockBlurParent") as FrameLayout
            val mHotseatEditingEnter = it.thisObject.getObjectField("mHotseatEditingEnter") as Animation
            val mHotseatEditingExit = it.thisObject.getObjectField("mHotseatEditingExit") as Animation
            val z = it.args[1] as Boolean
            if (mDockBlurParent.visibility == 0 == z) {
                mDockBlurParent.startAnimation(if (z) mHotseatEditingExit else mHotseatEditingEnter)
                mDockBlurParent.visibility = if (z) View.INVISIBLE else View.VISIBLE
                if (z) {
                    return@hookAfterMethod
                }
                mDockBlurParent.alpha = 1.0f
            }
        }
        launcherClass.hookAfterMethod("setScreenContentAlpha", Float::class.java) {
            val mDockBlurParent = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDockBlurParent") as FrameLayout
            mDockBlurParent.alpha = it.args[0] as Float
        }
    }

}