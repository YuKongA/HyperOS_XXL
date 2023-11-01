package com.yuk.hyperOS_XXL.activity.pages

import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import com.yuk.HyperOS_XXL.R

@BMPage("SystemUIPage", hideMenu = false)
class SystemUIPage : BasePage() {
    override fun getTitle(): String {
        setTitle(getString(R.string.systemui))
        return getString(R.string.systemui)
    }

    override fun onCreate() {
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.systemui_statusbar_show_seconds, tipsId = R.string.systemui_statusbar_show_seconds_summary),
            SwitchV("systemui_statusbar_show_seconds", false)
        )
    }

}
