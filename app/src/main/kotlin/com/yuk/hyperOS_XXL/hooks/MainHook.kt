package com.yuk.hyperOS_XXL.hooks

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import com.yuk.hyperOS_XXL.hooks.modules.BaseHook
import com.yuk.hyperOS_XXL.hooks.modules.framework.MaxWallpaperScale
import com.yuk.hyperOS_XXL.hooks.modules.framework.RemoveScreenshotRestriction
import com.yuk.hyperOS_XXL.hooks.modules.framework.RemoveSmallWindowRestriction1
import com.yuk.hyperOS_XXL.hooks.modules.framework.corepatch.CorePatchMainHook
import com.yuk.hyperOS_XXL.hooks.modules.guardprovider.AntiDefraudAppManager
import com.yuk.hyperOS_XXL.hooks.modules.mediaeditor.RemoveCropRestriction
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.AlwaysShowStatusBarClock
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.AnimDurationRatio
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.BlurWhenOpenFolder
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.CategoryFeatures
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.DockBlur
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.DoubleTapToSleep
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.MinusOneOverlapMode
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.RecentViewRemoveCardAnim
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.RemoveSmallWindowRestriction2
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.ScrollIconName
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.SetDeviceLevel
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.ShortcutAddSmallWindow
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.ShowRealMemory
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.TaskViewCardSize
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.UnlockAnim
import com.yuk.hyperOS_XXL.hooks.modules.miuihome.UseCompleteBlur
import com.yuk.hyperOS_XXL.hooks.modules.packageinstaller.AllowUpdateSystemApp
import com.yuk.hyperOS_XXL.hooks.modules.packageinstaller.DisableCountCheck
import com.yuk.hyperOS_XXL.hooks.modules.packageinstaller.RemovePackageInstallerAds
import com.yuk.hyperOS_XXL.hooks.modules.packageinstaller.ShowMoreApkInfo
import com.yuk.hyperOS_XXL.hooks.modules.personalassistant.BlurWhenGotoMinusOne
import com.yuk.hyperOS_XXL.hooks.modules.powerkeeper.DisableDynamicRefreshRate
import com.yuk.hyperOS_XXL.hooks.modules.securitycenter.RemoveMacroBlacklist
import com.yuk.hyperOS_XXL.hooks.modules.securitycenter.ShowBatteryTemperature
import com.yuk.hyperOS_XXL.hooks.modules.securitycenter.SkipWarningWaitTime
import com.yuk.hyperOS_XXL.hooks.modules.settings.NotificationImportance
import com.yuk.hyperOS_XXL.hooks.modules.systemui.StatusBarShowSeconds
import com.yuk.hyperOS_XXL.hooks.modules.thememanager.RemoveThemeManagerAds
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val TAG = "HyperOS XXL"
private val PACKAGE_NAME_HOOKED = setOf(
    "android",
    "com.android.settings",
    "com.android.systemui",
    "com.android.thememanager",
    "com.miui.gallery",
    "com.miui.guardprovider",
    "com.miui.home",
    "com.miui.mediaeditor",
    "com.miui.packageinstaller",
    "com.miui.personalassistant",
    "com.miui.powerkeeper",
    "com.miui.screenshot",
    "com.miui.securitycenter"
)

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
        CorePatchMainHook().initZygote(startupParam)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName in PACKAGE_NAME_HOOKED) {
            // Init EzXHelper
            EzXHelper.initHandleLoadPackage(lpparam)
            EzXHelper.setLogTag(TAG)
            EzXHelper.setToastTag(TAG)
            // Init hooks
            when (lpparam.packageName) {
                "android" -> {
                    CorePatchMainHook().handleLoadPackage(lpparam)
                    initHooks(
                        MaxWallpaperScale,
                        RemoveSmallWindowRestriction1,
                        RemoveScreenshotRestriction,
                    )
                }

                "com.android.settings" -> {
                    initHooks(
                        NotificationImportance,
                    )
                }

                "com.android.systemui" -> {
                    initHooks(
                        StatusBarShowSeconds,
                    )
                }

                "com.android.thememanager" -> {
                    initHooks(
                        RemoveThemeManagerAds,
                    )
                }

                "com.miui.gallery" -> {
                    initHooks(
                        RemoveCropRestriction,
                    )
                }

                "com.miui.guardprovider" -> {
                    AntiDefraudAppManager().handleLoadPackage(lpparam)
                }

                "com.miui.home" -> {
                    initHooks(
                        SetDeviceLevel,
                        DoubleTapToSleep,
                        ScrollIconName,
                        AnimDurationRatio,
                        UnlockAnim,
                        RecentViewRemoveCardAnim,
                        CategoryFeatures,
                        ShortcutAddSmallWindow,
                        RemoveSmallWindowRestriction2,
                        BlurWhenOpenFolder,
                        AlwaysShowStatusBarClock,
                        TaskViewCardSize,
                        UseCompleteBlur,
                        MinusOneOverlapMode,
                        ShowRealMemory,
                        DockBlur
                    )
                }

                "com.miui.mediaeditor" -> {
                    initHooks(
                        RemoveCropRestriction,
                    )
                }

                "com.miui.packageinstaller" -> {
                    AllowUpdateSystemApp().handleLoadPackage(lpparam)
                    initHooks(
                        RemovePackageInstallerAds,
                        ShowMoreApkInfo,
                        DisableCountCheck,
                    )
                }

                "com.miui.personalassistant" -> {
                    initHooks(
                        BlurWhenGotoMinusOne,
                    )
                }

                "com.miui.powerkeeper" -> {
                    initHooks(
                        DisableDynamicRefreshRate,
                    )
                }

                "com.miui.screenshot" -> {
                    initHooks(
                        RemoveCropRestriction,
                    )
                }

                "com.miui.securitycenter" -> {
                    RemoveMacroBlacklist().handleLoadPackage(lpparam)
                    initHooks(
                        SkipWarningWaitTime,
                        ShowBatteryTemperature,
                    )
                }

                else -> return
            }
        }
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            runCatching {
                if (it.isInit) return@forEach
                it.init()
                it.isInit = true
                Log.i("Inited hook: ${it.javaClass.simpleName}")
            }.logexIfThrow("Failed init hook: ${it.javaClass.simpleName}")
        }
    }

}
