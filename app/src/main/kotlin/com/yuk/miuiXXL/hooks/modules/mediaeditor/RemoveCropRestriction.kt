package com.yuk.miuiXXL.hooks.modules.mediaeditor

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.yuk.miuiXXL.hooks.modules.BaseHook
import com.yuk.miuiXXL.utils.getBoolean

object RemoveCropRestriction : BaseHook() {
    override fun init() {

        if (!getBoolean("mediaeditor_remove_crop_restriction", false)) return
        val resizeDetectorCropClass = loadClassOrNull("com.miui.gallery.editor.photo.core.imports.obsoletes.Crop\$ResizeDetector")
        val resizeDetectorScreenCropViewClass = loadClassOrNull("com.miui.gallery.editor.photo.screen.crop.ScreenCropView\$ResizeDetector")

        if (resizeDetectorCropClass != null) {
            resizeDetectorCropClass.methodFinder().filterByName("calculateMinSize").first().createHook {
                returnConstant(0)
            }
        } else {
            var resizeDetectorCrop = 'a'
            for (i in 0..25) {
                try {
                    loadClass("com.miui.gallery.editor.photo.core.imports.obsoletes.Crop\$${resizeDetectorCrop}").methodFinder()
                        .filterByReturnType(Int::class.java)
                        .filterByParamCount(0).first().createHook {
                            returnConstant(0)
                        }
                } catch (t: Throwable) {
                    resizeDetectorCrop++
                }
            }
        }
        if (resizeDetectorScreenCropViewClass != null) {
            resizeDetectorScreenCropViewClass.methodFinder().filterByName("calculateMinSize").first().createHook {
                returnConstant(0)
            }
        } else {
            var resizeDetectorScreenCropView = 'a'
            for (i in 0..25) {
                try {
                    loadClass("com.miui.gallery.editor.photo.screen.crop.ScreenCropView\$${resizeDetectorScreenCropView}").methodFinder()
                        .filterByReturnType(Int::class.java)
                        .filterByParamCount(0).first().createHook {
                            returnConstant(0)
                        }
                } catch (t: Throwable) {
                    resizeDetectorScreenCropView++
                }
            }
        }
    }

}
