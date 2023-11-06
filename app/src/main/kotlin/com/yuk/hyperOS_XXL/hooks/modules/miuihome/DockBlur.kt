package com.yuk.hyperOS_XXL.hooks.modules.miuihome

import android.animation.ValueAnimator
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import cn.fkj233.ui.activity.dp2px
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.EzXHelper.appContext
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.yuk.hyperOS_XXL.blur.BlurView
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.callMethod
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.findClass
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.hookAfterMethod
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.hookBeforeMethod
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean
import de.robv.android.xposed.XposedHelpers

object DockBlur : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_add_dock_blur", false)) return

        var isShowEditPanel = false
        val launcherClass = "com.miui.home.launcher.Launcher".findClass()
        val folderInfo = "com.miui.home.launcher.FolderInfo".findClass()

        Application::class.java.hookBeforeMethod("attach", Context::class.java) { hookParam ->
            EzXHelper.initAppContext(hookParam.args[0] as Context)

            val mDockHeight = dp2px(appContext, 94f)
            val mDockMargin = dp2px(appContext, 10f)
            val mDockBottomMargin = dp2px(appContext, 14f)

            // DockBlur WIP
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
                launcherClass.hookAfterMethod("onCreate", Bundle::class.java) {
                    val mSearchBarContainer = it.thisObject.callMethod("getSearchBarContainer") as FrameLayout
                    val mSearchEdgeLayout = mSearchBarContainer.parent as FrameLayout
                    val mDockBlurParent = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDockBlurParent") as FrameLayout
                    val mDockBlur = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDockBlur") as BlurView
                    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mDockHeight)
                    lp.gravity = Gravity.BOTTOM
                    lp.setMargins(mDockMargin, 0, mDockMargin, mDockBottomMargin)
                    mDockBlurParent.layoutParams = lp
                    mDockBlur.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                    mSearchEdgeLayout.addView(mDockBlurParent, 0)
                    mDockBlurParent.addView(mDockBlur, 0)
                    launcherClass.hookAfterMethod("showEditPanel", Boolean::class.java) { hookParam ->
                        isShowEditPanel = hookParam.args[0] as Boolean
                        if (isShowEditPanel) {
                            val valueAnimator = ValueAnimator.ofFloat(mDockBlurParent.alpha, 0f)
                            valueAnimator.addUpdateListener { animator ->
                                val value = animator.animatedValue as Float
                                mDockBlurParent.alpha = value
                            }
                            valueAnimator.duration = 150
                            valueAnimator.start()
                        } else {
                            val valueAnimator = ValueAnimator.ofFloat(mDockBlurParent.alpha, 1f)
                            valueAnimator.addUpdateListener { animator ->
                                val value = animator.animatedValue as Float
                                mDockBlurParent.alpha = value
                            }
                            valueAnimator.duration = 150
                            valueAnimator.start()
                        }
                    }
                    launcherClass.hookAfterMethod("openFolder", folderInfo, View::class.java) {
                        if (!isShowEditPanel) {
                            val valueAnimator = ValueAnimator.ofFloat(mDockBlurParent.alpha, 0f)
                            valueAnimator.addUpdateListener { animator ->
                                val value = animator.animatedValue as Float
                                mDockBlurParent.alpha = value
                            }
                            valueAnimator.duration = 150
                            valueAnimator.start()
                        }
                    }
                    launcherClass.hookAfterMethod("closeFolder", Boolean::class.java) {
                        if (!isShowEditPanel) {
                            val valueAnimator = ValueAnimator.ofFloat(mDockBlurParent.alpha, 1f)
                            valueAnimator.addUpdateListener { animator ->
                                val value = animator.animatedValue as Float
                                mDockBlurParent.alpha = value
                            }
                            valueAnimator.duration = 150
                            valueAnimator.start()
                        }
                    }
                }
            }
        }

    }
}