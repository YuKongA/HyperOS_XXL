package com.yuk.hyperOS_XXL.hooks.modules.systemui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.provider.Settings
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean
import de.robv.android.xposed.XC_MethodHook
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask

object LockScreenShowSeconds : BaseHook() {
    private var nowTime: Date = Calendar.getInstance().time

    override fun init() {
        if (!getBoolean("systemui_lockscreen_show_seconds", false)) return
        loadClass("com.miui.clock.MiuiBaseClock").constructorFinder().filterByParamCount(2).first().createHook {
            after {
                try {
                    val viewGroup = it.thisObject as LinearLayout?
                    if (viewGroup != null) {
                        val d: Method = viewGroup.javaClass.getDeclaredMethod("updateTime")
                        val r = Runnable {
                            d.isAccessible = true
                            d.invoke(viewGroup)
                        }

                        class T : TimerTask() {
                            override fun run() {
                                Handler(viewGroup.context.mainLooper).post(r)
                            }
                        }
                        Timer().scheduleAtFixedRate(T(), 1000 - System.currentTimeMillis() % 1000, 1000)
                    }
                } catch (_: Nothing) {
                }
            }
        }

        loadClass("com.miui.clock.MiuiLeftTopClock").methodFinder().filterByName("updateTime").first().createHook {
            after { updateTime(it, false) }
        }

        loadClass("com.miui.clock.MiuiCenterHorizontalClock").methodFinder().filterByName("updateTime").first().createHook {
            after { updateTime(it, false) }
        }

        loadClass("com.miui.clock.MiuiLeftTopLargeClock").methodFinder().filterByName("updateTime").first().createHook {
            after { updateTime(it, false) }
        }

        loadClass("com.miui.clock.MiuiVerticalClock").methodFinder().filterByName("updateTime").first().createHook {
            after { updateTime(it, true) }
        }
    }

    private fun updateTime(it: XC_MethodHook.MethodHookParam, isVertical: Boolean) {
        val textV = it.thisObject.objectHelper().getObjectOrNullAs<TextView>("mTimeText")!!
        val c: Context = textV.context
        val is24 = Settings.System.getString(c.contentResolver, Settings.System.TIME_12_24) == "24"
        nowTime = Calendar.getInstance().time
        textV.text = getTime(is24, isVertical)
    }


    @SuppressLint("SimpleDateFormat")
    private fun getTime(is24: Boolean, isVertical: Boolean): String {
        var timePattern = ""
        timePattern += if (isVertical) { //垂直
            if (is24) "HH\nmm\nss" else "hh\nmm\nss"
        } else { //水平
            if (is24) "HH:mm:ss" else "h:mm:ss"
        }
        timePattern = SimpleDateFormat(timePattern).format(nowTime)
        return timePattern
    }

}
