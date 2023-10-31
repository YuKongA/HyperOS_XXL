package com.yuk.hyperOS_XXL.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.dialog.MIUIDialog
import com.yuk.HyperOS_XXL.R
import com.yuk.hyperOS_XXL.activity.pages.AndroidPage
import com.yuk.hyperOS_XXL.activity.pages.MainPage
import com.yuk.hyperOS_XXL.activity.pages.MediaEditorPage
import com.yuk.hyperOS_XXL.activity.pages.MiuiHomePage
import com.yuk.hyperOS_XXL.activity.pages.PackageInstallerPage
import com.yuk.hyperOS_XXL.activity.pages.PersonalAssistantPage
import com.yuk.hyperOS_XXL.activity.pages.PowerKeeperPage
import com.yuk.hyperOS_XXL.activity.pages.SecurityCenterPage
import com.yuk.hyperOS_XXL.activity.pages.SettingsPage
import com.yuk.hyperOS_XXL.activity.pages.SystemUIPage
import com.yuk.hyperOS_XXL.activity.pages.ThemeManagerPage
import com.yuk.hyperOS_XXL.utils.AppUtils.perfFileName
import com.yuk.hyperOS_XXL.utils.BackupUtils
import kotlin.system.exitProcess

class MainActivity : MIUIActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        checkLSPosed()
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("WorldReadableFiles")
    private fun checkLSPosed() {
        try {
            setSP(getSharedPreferences(perfFileName(), MODE_WORLD_READABLE))
        } catch (exception: SecurityException) {
            isLoad = false
            MIUIDialog(this) {
                setTitle(R.string.tips)
                setMessage(R.string.not_support)
                setCancelable(false)
                setRButton(R.string.done) {
                    exitProcess(0)
                }
            }.show()
        }
    }

    init {
        activity = this
        registerPage(MainPage::class.java)
        registerPage(AndroidPage::class.java)
        registerPage(MiuiHomePage::class.java)
        registerPage(PowerKeeperPage::class.java)
        registerPage(SecurityCenterPage::class.java)
        registerPage(SystemUIPage::class.java)
        registerPage(ThemeManagerPage::class.java)
        registerPage(SettingsPage::class.java)
        registerPage(MediaEditorPage::class.java)
        registerPage(PersonalAssistantPage::class.java)
        registerPage(PackageInstallerPage::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode == RESULT_OK) {
            when (requestCode) {
                BackupUtils.CREATE_DOCUMENT_CODE -> {
                    BackupUtils.handleCreateDocument(activity, data.data)
                }

                BackupUtils.OPEN_DOCUMENT_CODE -> {
                    BackupUtils.handleReadDocument(activity, data.data)
                }

            }
        }
    }

}
