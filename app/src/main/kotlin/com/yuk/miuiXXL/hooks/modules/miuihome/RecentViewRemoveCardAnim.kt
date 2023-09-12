package com.yuk.miuiXXL.hooks.modules.miuihome

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.MotionEvent
import android.view.View
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.KotlinXposedHelper.callMethod
import com.yuk.miuiXXL.utils.KotlinXposedHelper.callStaticMethod
import com.yuk.miuiXXL.utils.KotlinXposedHelper.findClass
import com.yuk.miuiXXL.utils.KotlinXposedHelper.hookAfterMethod
import com.yuk.miuiXXL.utils.KotlinXposedHelper.new
import com.yuk.miuiXXL.utils.KotlinXposedHelper.replaceMethod
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean

object RecentViewRemoveCardAnim : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_recentview_remove_card_animation", false)) return
        "com.miui.home.recents.views.SwipeHelperForRecents".hookAfterMethod("onTouchEvent", MotionEvent::class.java) {
            val mCurrView = it.thisObject.objectHelper().getObjectOrNullAs<View?>("mCurrView")
            if (mCurrView != null) {
                mCurrView.alpha = 1f
                mCurrView.scaleX = 1f
                mCurrView.scaleY = 1f
            }
        }

        "com.miui.home.recents.TaskStackViewLayoutStyleHorizontal".replaceMethod("createScaleDismissAnimation", View::class.java, Float::class.java) {
            val view = it.args[0] as View
            val getScreenHeight = "com.miui.home.launcher.DeviceConfig".findClass().callStaticMethod("getScreenHeight") as Int
            val ofFloat = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.translationY, -getScreenHeight * 1.1484375f)
            val physicBasedInterpolator = "com.miui.home.launcher.anim.PhysicBasedInterpolator".findClass().new(0.9f, 0.78f)
            ofFloat.interpolator = physicBasedInterpolator as TimeInterpolator
            ofFloat.duration = 400
            return@replaceMethod ofFloat
        }

        "com.miui.home.recents.views.VerticalSwipe".hookAfterMethod("calculate", Float::class.java) {
            val f = it.args[0] as Float
            val asScreenHeightWhenDismiss = "com.miui.home.recents.views.VerticalSwipe".findClass().callStaticMethod("getAsScreenHeightWhenDismiss") as Int
            val f2 = f / asScreenHeightWhenDismiss
            val mTaskViewHeight = it.thisObject.objectHelper().getObjectOrNullAs<Float>("mTaskViewHeight")
            val mCurScale = it.thisObject.objectHelper().getObjectOrNullAs<Float>("mCurScale")
            val f3: Float = mTaskViewHeight!! * mCurScale!!
            val i = if (f2 > 0.0f) 1 else if (f2 == 0.0f) 0 else -1
            val afterFrictionValue: Float = it.thisObject.callMethod("afterFrictionValue", f, asScreenHeightWhenDismiss) as Float
            if (i < 0) it.thisObject.objectHelper().setObject("mCurTransY", (mTaskViewHeight / 2.0f + afterFrictionValue * 2) - (f3 / 2.0f))
        }
    }

}
