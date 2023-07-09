package com.yuk.miuiXXL.hooks.modules.miuihome

import android.app.AndroidAppHelper
import android.content.ComponentName
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import com.github.kyuubiran.ezxhelper.ClassUtils.getStaticObjectOrNullAs
import com.github.kyuubiran.ezxhelper.ClassUtils.invokeStaticMethodBestMatch
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.setStaticObject
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHooks
import com.github.kyuubiran.ezxhelper.ObjectUtils.invokeMethodBestMatch
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.R
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean
import com.yuk.miuiXXL.utils.new

object ShortcutAddSmallWindow : BaseHook() {
    override fun init() {

        if (!getBoolean("miuihome_shortcut_add_small_window", false)) return
        val mViewDarkModeHelper = loadClass("com.miui.home.launcher.util.ViewDarkModeHelper")
        val mSystemShortcutMenu = loadClass("com.miui.home.launcher.shortcuts.SystemShortcutMenu")
        val mSystemShortcutMenuItem = loadClass("com.miui.home.launcher.shortcuts.SystemShortcutMenuItem")
        val mAppShortcutMenu = loadClass("com.miui.home.launcher.shortcuts.AppShortcutMenu")
        val mShortcutMenuItem = loadClass("com.miui.home.launcher.shortcuts.ShortcutMenuItem")
        val mAppDetailsShortcutMenuItem = loadClass("com.miui.home.launcher.shortcuts.SystemShortcutMenuItem\$AppDetailsShortcutMenuItem")
        val mActivityUtilsCompat = loadClass("com.miui.launcher.utils.ActivityUtilsCompat")

        mViewDarkModeHelper.methodFinder().filterByName("onConfigurationChanged").toList().createHooks {
            after {
                invokeStaticMethodBestMatch(mSystemShortcutMenuItem, "createAllSystemShortcutMenuItems")
            }
        }

        mShortcutMenuItem.methodFinder().filterByName("getShortTitle").toList().createHooks {
            after {
                if (it.result == "应用信息") it.result = "信息"
            }
        }

        mAppDetailsShortcutMenuItem.methodFinder().filterByName("getOnClickListener").toList().createHooks {
            before {
                val mShortTitle = invokeMethodBestMatch(it.thisObject, "getShortTitle") as CharSequence
                if (mShortTitle == moduleRes.getString(R.string.miuihome_shortcut_add_small_window_title)) {
                    it.result = View.OnClickListener { view ->
                        val mComponentName = invokeMethodBestMatch(it.thisObject, "getComponentName") as ComponentName
                        val intent = Intent()
                        intent.action = "android.intent.action.MAIN"
                        intent.addCategory("android.intent.category.LAUNCHER")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.component = mComponentName
                        val makeFreeformActivityOptions =
                            invokeStaticMethodBestMatch(mActivityUtilsCompat, "makeFreeformActivityOptions", null, view.context, mComponentName.packageName)
                        if (makeFreeformActivityOptions != null) view.context.startActivity(intent, invokeMethodBestMatch(makeFreeformActivityOptions, "toBundle") as Bundle)
                    }
                }
            }
        }

        mSystemShortcutMenu.methodFinder().filterByName("getMaxShortcutItemCount").toList().createHooks {
            returnConstant(5)
        }

        mAppShortcutMenu.methodFinder().filterByName("getMaxShortcutItemCount").toList().createHooks {
            returnConstant(5)
        }

        mSystemShortcutMenuItem.methodFinder().filterByName("createAllSystemShortcutMenuItems").toList().createHooks {
            after {
                val isDarkMode =
                    AndroidAppHelper.currentApplication().applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                val mAllSystemShortcutMenuItems = getStaticObjectOrNullAs<Collection<Any>>(mSystemShortcutMenuItem, "sAllSystemShortcutMenuItems")
                val mSmallWindowInstance = mAppDetailsShortcutMenuItem.new()
                invokeMethodBestMatch(mSmallWindowInstance, "setShortTitle", null, moduleRes.getString(R.string.miuihome_shortcut_add_small_window_title))
                invokeMethodBestMatch(
                    mSmallWindowInstance,
                    "setIconDrawable",
                    null,
                    if (isDarkMode) moduleRes.getDrawable(R.drawable.ic_miuihome_small_window_dark) else moduleRes.getDrawable(R.drawable.ic_miuihome_small_window_light)
                )
                val sAllSystemShortcutMenuItems = ArrayList<Any>()
                sAllSystemShortcutMenuItems.add(mSmallWindowInstance)
                sAllSystemShortcutMenuItems.addAll(mAllSystemShortcutMenuItems!!)
                setStaticObject(mSystemShortcutMenuItem, "sAllSystemShortcutMenuItems", sAllSystemShortcutMenuItems)
            }
        }
    }

}
