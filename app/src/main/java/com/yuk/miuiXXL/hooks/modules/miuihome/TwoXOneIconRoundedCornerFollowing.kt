package com.yuk.miuiXXL.hooks.modules.miuihome

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.getObjectField

object TwoXOneIconRoundedCornerFollowing : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_two_x_one_icon_rounded_corner_following", false)) return
        loadClass("com.miui.home.launcher.bigicon.BigIconUtil").methodFinder().filterByName("getCroppedFromCorner").filterByParamCount(4).first().createHook {
            before {
                it.args[0] = 2
                it.args[1] = 2
            }
        }

        loadClass("com.miui.home.launcher.maml.MaMlHostView").methodFinder().filterByName("getCornerRadius").first().createHook {
            before {
                it.result = it.thisObject.getObjectField("mEnforcedCornerRadius")
            }
        }

        loadClass("com.miui.home.launcher.maml.MaMlHostView").methodFinder().filterByName("computeRoundedCornerRadius").filterByParamCount(1).first()
            .createHook {
                before {
                    it.result = it.thisObject.getObjectField("mEnforcedCornerRadius")
                }
            }

        loadClass("com.miui.home.launcher.LauncherAppWidgetHostView").methodFinder().filterByName("computeRoundedCornerRadius").filterByParamCount(1).first()
            .createHook {
                before {
                    it.result = it.thisObject.getObjectField("mEnforcedCornerRadius")
                }
            }
    }

}
