package com.yuk.hyperOS_XXL.activity.pages

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import com.yuk.HyperOS_XXL.R

@BMPage("ThemeManagerPage", hideMenu = false)
class ThemeManagerPage : BasePage() {
    override fun getTitle(): String {
        setTitle(getString(R.string.thememanager))
        return getString(R.string.thememanager)
    }

    override fun onCreate() {
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.thememanager_remove_ads, tipsId = R.string.thememanager_remove_ads_summary),
            SwitchV("thememanager_remove_ads", false)
        )
    }

}
