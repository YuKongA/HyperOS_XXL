package com.yuk.hyperOS_XXL.hooks.modules

abstract class BaseHook {
    var isInit: Boolean = false
    abstract fun init()
}
