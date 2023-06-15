package com.yuk.miuiXXL.hooks.modules.systemui

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.setObjectField

object WaveChargeAnim : BaseHook() {
    override fun init() {

        if (!getBoolean("systemui_wave_charge_animation", false)) return
        loadClass("com.android.keyguard.charge.ChargeUtils").methodFinder().filterByName("supportWaveChargeAnimation").first().createHook {
            after {
                val stackTrace = Throwable().stackTrace
                var mResult = false
                val classTrue = setOf("com.android.keyguard.charge.ChargeUtils", "com.android.keyguard.charge.container.MiuiChargeContainerView")
                for (i in stackTrace.indices) {
                    when (stackTrace[i].className) {
                        in classTrue -> {
                            mResult = true
                            break
                        }
                    }
                }
                it.result = mResult
            }
        }
        loadClass("com.android.keyguard.charge.wave.WaveView").methodFinder().filterByName("updateWaveHeight").first().createHook {
            after {
                it.thisObject.setObjectField("mWaveXOffset", 0)
            }
        }

    }

}
