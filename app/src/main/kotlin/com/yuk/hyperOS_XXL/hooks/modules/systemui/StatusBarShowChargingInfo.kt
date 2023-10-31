package com.yuk.hyperOS_XXL.hooks.modules.systemui

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.BatteryManager
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.PathInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.AppUtils.getBatteryCurrent
import com.yuk.hyperOS_XXL.utils.AppUtils.getBatteryTemperature
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.callMethodAs
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.getObjectFieldAs
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean

object StatusBarShowChargingInfo : BaseHook() {

    @SuppressLint("SetTextI18n")
    override fun init() {

        var textView: TextView? = null
        var context: Context? = null

        if (!getBoolean("systemui_statusbar_show_charge_info", false)) return
        loadClass("com.android.systemui.statusbar.phone.DarkIconDispatcherImpl").methodFinder().filterByName("applyIconTint").first().createHook {
            after {
                val color = it.thisObject.getObjectFieldAs<Int>("mIconTint")
                if (textView != null) textView!!.setTextColor(color)
            }
        }

        loadClass("com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView").constructorFinder().filterByParamCount(2).first().createHook {
            after {
                context = it.args[0] as Context?
            }
        }
        loadClass("com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView").methodFinder().filterByName("onFinishInflate").first().createHook {
            after {
                val mStatusBarLeftContainer = it.thisObject.getObjectFieldAs<LinearLayout>("mStatusBarLeftContainer")
                val mView = mStatusBarLeftContainer.getChildAt(1) as View
                textView = TextView(context!!).apply {
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7.6f)
                    typeface = Typeface.create(null, 800, false)
                    isSingleLine = false
                    setLineSpacing(0f, 0.7f)
                    layoutParams = mView.layoutParams
                }
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_BATTERY_CHANGED)
                }
                context!!.registerReceiver(BatteryReceiver(textView!!), filter)
                mStatusBarLeftContainer.addView(textView, 4)
            }
        }
        loadClass("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment").methodFinder().filterByName("showClock").first().createHook {
            after {
                val batteryBroadcast = context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.getIntExtra("status", 0)
                if (it.args[0] as Boolean && batteryBroadcast == BatteryManager.BATTERY_STATUS_CHARGING) {
                    textView!!.visibility = View.VISIBLE
                    val interpolator: Interpolator = PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f)
                    textView!!.animate()?.alpha(1.0f)?.setDuration(250L)?.setInterpolator(interpolator)
                }
            }
        }
        loadClass("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment").methodFinder().filterByName("hideClock").first().createHook {
            after {
                if (it.args[0] as Boolean) {
                    val interpolator: Interpolator = PathInterpolator(0.0f, 0.0f, 0.8f, 1.0f)
                    textView!!.animate()?.alpha(0.0f)?.setDuration(160L)?.setInterpolator(interpolator).also { animator ->
                        animator?.withEndAction {
                            textView!!.visibility = View.GONE
                        }
                    }
                }
            }
        }
        loadClass("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment").methodFinder().filterByName("updateNotificationIconAreaAndCallChip")
            .first().createHook {
                after {
                    val clockHiddenMode = it.thisObject.callMethodAs<Int>("clockHiddenMode")
                    if (it.args[0] != 0) {
                        if (clockHiddenMode == 4 || textView!!.alpha == 0.0f) {
                            textView!!.visibility = View.GONE
                        }
                    } else {
                        val batteryBroadcast = context!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.getIntExtra("status", 0)
                        if (batteryBroadcast == BatteryManager.BATTERY_STATUS_CHARGING) {
                            textView!!.alpha = 1.0f
                            textView!!.visibility = View.VISIBLE
                        }
                    }
                }
            }
    }

    class BatteryReceiver(private val textView: TextView) : BroadcastReceiver() {

        val handler = Handler(textView.context.mainLooper)

        private val runnable = object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                val temperature = getBatteryTemperature()
                val current = getBatteryCurrent(textView.context)
                textView.text = "${"%.2f".format(current)}A\n${"%.1f".format(temperature)}℃"
                if (textView.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    textView.setPadding(5, 14, 0, 0)
                } else if (textView.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textView.setPadding(5, 20, 0, 0)
                }
                handler.postDelayed(this, 1500)
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getIntExtra("status", 0)
            val mKeyguardManager = textView.context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            val isInLockScreen: Boolean = mKeyguardManager.inKeyguardRestrictedInputMode()
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                if (textView.alpha != 0.0f && !isInLockScreen) {
                    textView.visibility = View.VISIBLE
                }
            } else {
                textView.visibility = View.GONE
            }
            if (status == BatteryManager.BATTERY_STATUS_CHARGING && !isInLockScreen && textView.alpha != 0.0f) {
                handler.post(runnable)
            }
            if (status == BatteryManager.BATTERY_STATUS_DISCHARGING || isInLockScreen) {
                handler.removeCallbacks(runnable)
            }
        }
    }

}
