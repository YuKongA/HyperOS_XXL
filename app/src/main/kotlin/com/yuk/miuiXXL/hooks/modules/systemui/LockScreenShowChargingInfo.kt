package com.yuk.miuiXXL.hooks.modules.systemui

import android.annotation.SuppressLint
import android.app.AndroidAppHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Handler
import android.os.PowerManager
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.ClassUtils.invokeStaticMethodBestMatch
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.ObjectUtils.invokeMethodBestMatch
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.AppUtils.getBatteryTemperature
import com.yuk.miuiXXL.utils.AppUtils.getBatteryVoltage
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean
import kotlin.math.abs

object LockScreenShowChargingInfo : BaseHook() {
    @SuppressLint("SetTextI18n")
    override fun init() {

        if (!getBoolean("systemui_lockscreen_show_current", false)) return
        loadClass("com.android.keyguard.charge.ChargeUtils").methodFinder().filterByName("getChargingHintText").filterByParamCount(3).first().createHook {
            after {
                val text = (it.result as? String)?.replace("%", " %")?.replace("正在充电 ", "充电中 · ")?.replace("正在极速充电 ", "快充中 · ") ?: return@after
                if (text.startsWith("预计可用")) return@after
                it.result = text + getChargingInfo()
            }
        }
        try {
            loadClass("com.android.systemui.statusbar.phone.KeyguardIndicationTextView").constructors.createHooks {
                after {
                    (it.thisObject as TextView).isSingleLine = false
                    val screenOnOffReceiver = object : BroadcastReceiver() {
                        val keyguardIndicationController = invokeStaticMethodBestMatch(
                            loadClass("com.android.systemui.Dependency"), "get", null, loadClass("com.android.systemui.statusbar.KeyguardIndicationController")
                        )
                        val handler = Handler((it.thisObject as TextView).context.mainLooper)
                        val runnable = object : Runnable {
                            override fun run() {
                                if (keyguardIndicationController != null) {
                                    invokeMethodBestMatch(keyguardIndicationController, "updatePowerIndication")
                                }
                                handler.postDelayed(this, 1000)
                            }
                        }

                        init {
                            if (((it.thisObject as TextView).context.getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive) handler.post(runnable)
                        }

                        override fun onReceive(context: Context, intent: Intent) {
                            when (intent.action) {
                                Intent.ACTION_SCREEN_ON -> handler.post(runnable)
                                Intent.ACTION_SCREEN_OFF -> handler.removeCallbacks(runnable)
                            }
                        }
                    }
                    val filter = IntentFilter().apply {
                        addAction(Intent.ACTION_SCREEN_ON)
                        addAction(Intent.ACTION_SCREEN_OFF)
                    }
                    (it.thisObject as TextView).context.registerReceiver(screenOnOffReceiver, filter)
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun getChargingInfo(): String {
        val batteryManager = AndroidAppHelper.currentApplication().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val current = abs(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000.0)
        val voltage = getBatteryVoltage()
        val temperature = getBatteryTemperature()
        val watt = current / 1000 * voltage
        return String.format(" · %.1f ℃\n%.0f mA · %.1f V · %.1f W", temperature, current, voltage, watt)
    }

}
