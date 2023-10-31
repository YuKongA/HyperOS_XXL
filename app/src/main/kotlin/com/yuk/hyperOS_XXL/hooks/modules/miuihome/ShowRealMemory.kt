package com.yuk.hyperOS_XXL.hooks.modules.miuihome

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.text.format.Formatter
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.getObjectField
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean

object ShowRealMemory : BaseHook() {

    @SuppressLint("DiscouragedApi")
    override fun init() {

        var context: Context? = null

        if (!getBoolean("miuihome_show_real_memory", false)) return
        loadClass("com.miui.home.recents.views.RecentsContainer").constructorFinder().filterByParamCount(2).first().createHook {
            after {
                context = it.args[0] as Context
            }
        }

        loadClass("com.miui.home.recents.views.RecentsContainer").methodFinder().filterByName("refreshMemoryInfo").first().createHook {
            before {
                val memoryInfo = ActivityManager.MemoryInfo()
                val activityManager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                activityManager.getMemoryInfo(memoryInfo)
                val totalMem = Formatter.formatFileSize(context, memoryInfo.totalMem)
                val availMem = Formatter.formatFileSize(context, memoryInfo.availMem)
                val mTxtMemoryInfo1 = it.thisObject.getObjectField("mTxtMemoryInfo1") as TextView
                mTxtMemoryInfo1.text = context!!.getString(context!!.resources.getIdentifier("status_bar_recent_memory_info1", "string", "com.miui.home"), availMem, totalMem)
                val mTxtMemoryInfo2 = it.thisObject.getObjectField("mTxtMemoryInfo2") as TextView
                mTxtMemoryInfo2.text = context!!.getString(context!!.resources.getIdentifier("status_bar_recent_memory_info2", "string", "com.miui.home"), availMem, totalMem)
                it.result = null
            }
        }
    }

}
