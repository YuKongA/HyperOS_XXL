package com.yuk.miuiXXL.utils

import com.yuk.miuiXXL.BuildConfig
import de.robv.android.xposed.XSharedPreferences

object XSharedPreferences {
    private fun prefs() = XSharedPreferences(BuildConfig.APPLICATION_ID, "MiuiXXL_Config")
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        if (prefs().hasFileChanged()) prefs().reload()
        return prefs().getBoolean(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        if (prefs().hasFileChanged()) prefs().reload()
        return prefs().getInt(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        if (prefs().hasFileChanged()) prefs().reload()
        return prefs().getFloat(key, defValue)
    }

    fun getString(key: String, defValue: String): String {
        if (prefs().hasFileChanged()) prefs().reload()
        return prefs().getString(key, defValue) ?: defValue
    }

    fun getStringSet(key: String, defValue: MutableSet<String>): MutableSet<String> {
        if (prefs().hasFileChanged()) prefs().reload()
        return prefs().getStringSet(key, defValue) ?: defValue
    }
}

