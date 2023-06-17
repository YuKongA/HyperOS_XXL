package com.yuk.miuiXXL.activity.pages

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMMainPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import com.yuk.miuiXXL.R
import com.yuk.miuiXXL.activity.MainActivity
import com.yuk.miuiXXL.utils.BackupUtils
import com.yuk.miuiXXL.utils.atLeastAndroidT
import com.yuk.miuiXXL.utils.exec

@BMMainPage("Miui XXL")
class MainPage : BasePage() {
    @SuppressLint("WorldReadableFiles")
    override fun onCreate() {
        Page(this.getDrawable(R.drawable.ic_android), TextSummaryV(textId = R.string.android, tipsId = R.string.android_reboot), round = 8f, onClickListener = { showFragment("AndroidPage") })
        Line()
        Page(this.getDrawable(R.drawable.ic_systemui), TextSummaryV(textId = R.string.systemui), round = 8f, onClickListener = { showFragment("SystemUIPage") })
        Page(this.getDrawable(R.drawable.ic_settings), TextSummaryV(textId = R.string.settings), round = 8f, onClickListener = { showFragment("SettingsPage") })
        Page(this.getDrawable(R.drawable.ic_miuihome), TextSummaryV(textId = R.string.miuihome), round = 8f, onClickListener = { showFragment("MiuiHomePage") })
        Page(this.getDrawable(R.drawable.ic_personalassistant), TextSummaryV(textId = R.string.personalassistant), round = 8f, onClickListener = { showFragment("PersonalAssistantPage") })
        Page(this.getDrawable(R.drawable.ic_securitycenter), TextSummaryV(textId = R.string.securitycenter), round = 8f, onClickListener = { showFragment("SecurityCenterPage") })
        Page(this.getDrawable(R.drawable.ic_thememanager), TextSummaryV(textId = R.string.thememanager), round = 8f, onClickListener = { showFragment("ThemeManagerPage") })
        Page(this.getDrawable(R.drawable.ic_mediaeditor), TextSummaryV(textId = R.string.mediaeditor), round = 8f, onClickListener = { showFragment("MediaEditorPage") })
        Page(this.getDrawable(R.drawable.ic_powerkeeper), TextSummaryV(textId = R.string.powerkeeper), round = 8f, onClickListener = { showFragment("PowerKeeperPage") })
        Page(this.getDrawable(R.drawable.ic_packageinstaller), TextSummaryV(textId = R.string.packageinstaller), round = 8f, onClickListener = { showFragment("PackageInstallerPage") })
        Line()
        TextWithSwitch(TextV(textId = R.string.hide_desktop_icon), SwitchV("hide_desktop_icon", onClickListener = {
            val pm = MIUIActivity.activity.packageManager
            val mComponentEnabledState: Int = if (it) {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }
            pm.setComponentEnabledSetting(ComponentName(MIUIActivity.activity, MainActivity::class.java.name + "Alias"), mComponentEnabledState, PackageManager.DONT_KILL_APP)
        }))
        TextWithArrow(TextV(textId = R.string.backup, onClickListener = {
            BackupUtils.backup(activity, activity.createDeviceProtectedStorageContext().getSharedPreferences("MiuiXXL_Config", Context.MODE_WORLD_READABLE))
        }))
        TextWithArrow(TextV(textId = R.string.recovery, onClickListener = {
            BackupUtils.recovery(activity, activity.createDeviceProtectedStorageContext().getSharedPreferences("MiuiXXL_Config", Context.MODE_WORLD_READABLE))
        }))
        TextWithArrow(TextV(textId = R.string.restart_scope) {
            MIUIDialog(activity) {
                setTitle(R.string.tips)
                setMessage(R.string.restart_scope_summary)
                setLButton(R.string.cancel) {
                    dismiss()
                }
                setRButton(R.string.done) {
                    val command = arrayOf(
                        "am force-stop com.android.thememanager",
                        "am force-stop com.android.settings",
                        "am force-stop com.miui.gallery",
                        "am force-stop com.miui.guardprovider",
                        "am force-stop com.miui.home",
                        "am force-stop com.miui.mediaeditor",
                        "am force-stop com.miui.packageinstaller",
                        "am force-stop com.miui.personalassistant",
                        "am force-stop com.miui.powerkeeper",
                        "am force-stop com.miui.screenshot",
                        "am force-stop com.miui.securitycenter",
                        "killall com.android.systemui"
                    )
                    exec(command)
                    Toast.makeText(activity, getString(R.string.restart_scope_finished), Toast.LENGTH_SHORT).show()
                    Thread.sleep(500)
                    dismiss()
                }
            }.show()
        })
        TextWithArrow(TextV(textId = R.string.reboot_system) {
            MIUIDialog(activity) {
                setTitle(R.string.tips)
                setMessage(R.string.reboot_system_summary)
                setLButton(R.string.cancel) {
                    dismiss()
                }
                setRButton(R.string.done) {
                    exec("reboot")
                }
            }.show()
        })
    }

}
