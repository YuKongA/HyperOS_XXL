package com.yuk.miuiXXL.hooks.modules.thememanager

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.XSharedPreferences.getBoolean

object FuckValidateTheme1 : BaseHook() {
    override fun init() {

        if (!getBoolean("thememanager_fuck_validate_theme", false)) return
        try {
            loadClass("com.android.thememanager.detail.theme.model.OnlineResourceDetail").methodFinder().filterByName("toResource").toList().createHooks {
                after {
                    it.thisObject.objectHelper().setObject("bought", true)
                }
            }
        } catch (t: Throwable) {
            Log.ex(t)
        }
        try {
            loadClass("com.android.thememanager.basemodule.views.DiscountPriceView").methodFinder().filterByParamCount(2)
                .filterByParamTypes(Int::class.java, Int::class.java).filterByReturnType(Void.TYPE).toList().createHooks {
                    before {
                        it.args[1] = 0
                    }
                }
        } catch (t: Throwable) {
            Log.ex(t)
        }
        try {
            loadClass("com.android.thememanager.recommend.view.listview.viewholder.LargeIconDetailRecommendViewHolder").methodFinder().filterByParamCount(1)
                .filterByParamTypes(Int::class.java).filterByReturnType(Void.TYPE).toList().createHooks {
                    before {
                        it.args[0] = 0
                    }
                }
        } catch (t: Throwable) {
            Log.ex(t)
        }
        try {
            loadClass("com.miui.maml.widget.edit.MamlutilKt").methodFinder().filterByName("themeManagerSupportPaidWidget").first().createHook {
                returnConstant(false)
            }
        } catch (t: Throwable) {
            Log.ex(t)
        }
    }

}
