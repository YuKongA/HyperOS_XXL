package com.yuk.hyperOS_XXL.activity.pages

import android.view.View
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.view.SwitchV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import com.yuk.HyperOS_XXL.R

@BMPage("PersonalAssistantPage", hideMenu = false)
class PersonalAssistantPage : BasePage() {
    override fun getTitle(): String {
        setTitle(getString(R.string.personalassistant))
        return getString(R.string.personalassistant)
    }

    override fun onCreate() {
        val blurBinding = GetDataBinding({ MIUIActivity.safeSP.getBoolean("personalassistant_minus_one_blur", false) }) { view, flags, data ->
            if (flags == 1) view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextWithSwitch(
            TextV(textId = R.string.personalassistant_minus_one_blur),
            SwitchV("personalassistant_minus_one_blur", false, dataBindingSend = blurBinding.bindingSend)
        )
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.miuihome_minus_one_overlap_mode, tipsId = R.string.miuihome_minus_one_overlap_mode_summary),
            SwitchV("miuihome_minus_one_overlap_mode", false),
            dataBindingRecv = blurBinding.binding.getRecv(1)
        )
    }

}
