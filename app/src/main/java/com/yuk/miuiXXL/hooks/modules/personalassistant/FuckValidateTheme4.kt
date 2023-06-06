package com.yuk.miuiXXL.hooks.modules.personalassistant

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean

object FuckValidateTheme4 : BaseHook() {
    override fun init() {

        if (!getBoolean("thememanager_fuck_validate_theme", false)) return
        try {
            loadClass("com.miui.maml.widget.edit.MamlutilKt").methodFinder().filterByName("themeManagerSupportPaidWidget").first().createHook {
                returnConstant(false)
            }
            loadClass("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel").methodFinder().filterByName("isCanDirectAddMaMl").first()
                .createHook {
                    returnConstant(true)
                }
            loadClass("com.miui.personalassistant.picker.business.detail.utils.PickerDetailDownloadManager\$Companion").methodFinder()
                .filterByName("isCanDownload").first().createHook {
                    returnConstant(true)
                }
            loadClass("com.miui.personalassistant.picker.business.detail.utils.PickerDetailUtil").methodFinder().filterByName("isCanAutoDownloadMaMl").first()
                .createHook {
                    returnConstant(true)
                }
            loadClass("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponse").methodFinder().filterByName("isPay").first().createHook {
                returnConstant(false)
            }
            loadClass("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponse").methodFinder().filterByName("isBought").first()
                .createHook {
                    returnConstant(true)
                }
            loadClass("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponseWrapper").methodFinder().filterByName("isPay").first()
                .createHook {
                    returnConstant(false)
                }
            loadClass("com.miui.personalassistant.picker.business.detail.bean.PickerDetailResponseWrapper").methodFinder().filterByName("isBought").first()
                .createHook {
                    returnConstant(true)
                }
            loadClass("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel").methodFinder().filterByName("shouldCheckMamlBoughtState")
                .first().createHook {
                    returnConstant(false)
                }
            loadClass("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel").methodFinder()
                .filterByName("isTargetPositionMamlPayAndDownloading").first().createHook {
                    returnConstant(false)
                }
            loadClass("com.miui.personalassistant.picker.business.detail.PickerDetailViewModel").methodFinder()
                .filterByName("checkIsIndependentProcessWidgetForPosition").first().createHook {
                    returnConstant(true)
                }
        } catch (t: Throwable) {
            Log.ex(t)
        }
    }
}
