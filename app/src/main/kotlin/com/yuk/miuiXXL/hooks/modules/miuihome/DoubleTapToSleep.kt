package com.yuk.miuiXXL.hooks.modules.miuihome

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.callMethod
import com.yuk.miuiXXL.utils.callMethodAs
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.getObjectField
import de.robv.android.xposed.XposedHelpers

object DoubleTapToSleep : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_double_tap_to_sleep", false)) return
        val workspaceClass = loadClass("com.miui.home.launcher.Workspace")
        workspaceClass.constructorFinder().toList().createHooks {
            after {
                var mDoubleTapControllerEx = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDoubleTapControllerEx")
                if (mDoubleTapControllerEx != null) return@after
                mDoubleTapControllerEx = DoubleTapController(it.args[0] as Context)
                XposedHelpers.setAdditionalInstanceField(it.thisObject, "mDoubleTapControllerEx", mDoubleTapControllerEx)
            }
        }
        workspaceClass.methodFinder().filterByName("dispatchTouchEvent").filterByParamCount(1).first().createHook {
            before {
                val mDoubleTapControllerEx = XposedHelpers.getAdditionalInstanceField(it.thisObject, "mDoubleTapControllerEx") as DoubleTapController
                if (!mDoubleTapControllerEx.isDoubleTapEvent(it.args[0] as MotionEvent)) return@before
                val mCurrentScreenIndex = it.thisObject.getObjectField("mCurrentScreenIndex")
                val cellLayout = it.thisObject.callMethod("getCellLayout", mCurrentScreenIndex)
                if (cellLayout != null) if (cellLayout.callMethodAs<Boolean>("lastDownOnOccupiedCell")) return@before
                if (it.thisObject.callMethodAs<Boolean>("isInNormalEditingMode")) return@before
                val context = it.thisObject.callMethodAs<Context>("getContext")
                context.sendBroadcast(
                    Intent("com.miui.app.ExtraStatusBarManager.action_TRIGGER_TOGGLE").putExtra("com.miui.app.ExtraStatusBarManager.extra_TOGGLE_ID", 10)
                )
            }
        }
    }
}

class DoubleTapController internal constructor(mContext: Context) {
    private val maxDuration: Long = 500
    private var mActionDownRawX: Float = 0f
    private var mActionDownRawY: Float = 0f
    private var mClickCount: Int = 0
    private var mFirstClickRawX: Float = 0f
    private var mFirstClickRawY: Float = 0f
    private var mLastClickTime: Long = 0
    private val mTouchSlop: Int = ViewConfiguration.get(mContext).scaledTouchSlop * 2
    fun isDoubleTapEvent(motionEvent: MotionEvent): Boolean {
        val action = motionEvent.actionMasked
        return when {
            action == MotionEvent.ACTION_DOWN -> {
                mActionDownRawX = motionEvent.rawX
                mActionDownRawY = motionEvent.rawY
                false
            }

            action != MotionEvent.ACTION_UP -> false
            else -> {
                val rawX = motionEvent.rawX
                val rawY = motionEvent.rawY
                if (kotlin.math.abs(rawX - mActionDownRawX) <= mTouchSlop.toFloat() && kotlin.math.abs(
                        rawY - mActionDownRawY
                    ) <= mTouchSlop.toFloat()
                ) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime > maxDuration || rawY - mFirstClickRawY > mTouchSlop.toFloat() || rawX - mFirstClickRawX > mTouchSlop.toFloat()) mClickCount =
                        0
                    mClickCount++
                    if (mClickCount == 1) {
                        mFirstClickRawX = rawX
                        mFirstClickRawY = rawY
                        mLastClickTime = SystemClock.elapsedRealtime()
                        return false
                    } else if (kotlin.math.abs(rawY - mFirstClickRawY) <= mTouchSlop.toFloat() && kotlin.math.abs(
                            rawX - mFirstClickRawX
                        ) <= mTouchSlop.toFloat() && SystemClock.elapsedRealtime() - mLastClickTime <= maxDuration
                    ) {
                        mClickCount = 0
                        return true
                    }
                }
                mClickCount = 0
                false
            }
        }
    }

}
