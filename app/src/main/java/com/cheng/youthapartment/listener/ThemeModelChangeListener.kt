package com.cheng.youthapartment.listener

/**
 * 主题模式变化监听器
 * @author CHENG
 * @since 2025/4/5
 */
interface ThemeModelChangeListener {

    fun onThemeModelChanged(isNightModel: Boolean)
}