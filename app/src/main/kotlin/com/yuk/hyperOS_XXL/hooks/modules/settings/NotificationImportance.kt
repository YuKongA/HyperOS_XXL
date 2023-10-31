package com.yuk.hyperOS_XXL.hooks.modules.settings

import android.app.NotificationChannel
import com.github.kyuubiran.ezxhelper.ClassLoaderProvider
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.callMethod
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.findClass
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.hookAfterMethod
import com.yuk.hyperOS_XXL.utils.KotlinXposedHelper.hookBeforeAllMethods
import com.yuk.hyperOS_XXL.utils.XSharedPreferences.getBoolean
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

object NotificationImportance : BaseHook() {
    override fun init() {

        if (!getBoolean("settings_notification_importance", false)) return
        val mBaseNotificationSettings = "com.android.settings.notification.BaseNotificationSettings".findClass()
        val mChannelNotificationSettings = "com.android.settings.notification.ChannelNotificationSettings".findClass()
        mBaseNotificationSettings.hookBeforeAllMethods("setPrefVisible") {
            val pref = it.args[0]
            if (pref != null) {
                val prefKey = pref.callMethod("getKey") as String
                if ("importance" == prefKey) it.args[1] = true
            }
        }
        mChannelNotificationSettings.hookAfterMethod("setupChannelDefaultPrefs") {
            val pref = it.thisObject.callMethod("findPreference", "importance")
            it.thisObject.objectHelper().setObject("mImportance", pref)
            val mBackupImportance = it.thisObject.objectHelper().getObjectOrNull("mBackupImportance") as Int
            if (mBackupImportance > 0) {
                val index = pref?.callMethod("findSpinnerIndexOfValue", mBackupImportance.toString()) as Int
                if (index > -1) pref.callMethod("setValueIndex", index)
                val importanceListenerClass = ("androidx.preference.Preference\$OnPreferenceChangeListener").findClass()
                val handler = InvocationHandler { _, method, args ->
                    if (method.name == "onPreferenceChange") {
                        it.thisObject.objectHelper().setObject("mBackupImportance", (args[1] as String).toInt())
                        val mChannel = it.thisObject.objectHelper().getObjectOrNull("mChannel") as NotificationChannel
                        mChannel.importance = (args[1] as String).toInt()
                        mChannel.callMethod("lockFields", 4)
                        val mBackend = it.thisObject.objectHelper().getObjectOrNull("mBackend")
                        val mPkg = it.thisObject.objectHelper().getObjectOrNull("mPkg") as String
                        val mUid = it.thisObject.objectHelper().getObjectOrNull("mUid") as Int
                        mBackend?.callMethod("updateChannel", mPkg, mUid, mChannel)
                        it.thisObject.callMethod("updateDependents", false)
                    }
                    true
                }
                val mImportanceListener: Any = Proxy.newProxyInstance(ClassLoaderProvider.classLoader, arrayOf(importanceListenerClass), handler)
                pref.callMethod("setOnPreferenceChangeListener", mImportanceListener)
            }
        }

    }

}