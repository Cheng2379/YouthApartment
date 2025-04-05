package com.cheng.youthapartment.manager

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.cheng.youthapartment.App
import androidx.core.content.edit
import com.cheng.youthapartment.listener.ThemeModelChangeListener

/**
 * 主题模式管理类器, 监听主题和管理系统主题模式
 * @author CHENG
 * @since 2025/4/5
 */
object ThemeModelManager {
    private val themeModelListeners = mutableListOf<ThemeModelChangeListener>()
    private val mSp = App.getSharedPreferences()

    /**
     * 注册主题切换监听器
     * @param listener 监听器
     */
    fun registerThemeModelListener(listener: ThemeModelChangeListener) {
        if (!themeModelListeners.contains(listener)) {
            themeModelListeners.add(listener)
        }
    }

    /**
     * 反注册主题切换监听器
     * @param listener 监听器
     */
    fun unregisterThemeModelListener(listener: ThemeModelChangeListener) {
        themeModelListeners.remove(listener)
    }

    /**
     * 通知所有监听器主题模式已变化
     * @param isNightModel 是否为夜间模式
     */
    fun notifyThemeModelChanged(isNightModel: Boolean) {
        themeModelListeners.forEach {
            it.onThemeModelChanged(isNightModel)
        }
    }

    /**
     * 切换应用主题模式
     */
    fun toggleAppThemeModel() {
        setAppThemeMode(!isNightModel())
    }

    /**
     * 设置应用主题模式
     * @param isNightMode 是否设置为夜间模式
     */
    fun setAppThemeMode(isNightMode: Boolean) {
        val mode = if (isNightMode)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(mode)
        App.getSharedPreferences().edit {
            putBoolean(
                "is_night_model",
                isNightMode
            )
        }
        notifyThemeModelChanged(isNightMode)
    }

    /**
     * 获取当前主题
     * @return 是否为黑暗模式
     */
    fun isNightModel(): Boolean {
        // 获取系统当前模式状态值
        val currentNightMode =
            App.mContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isSystemNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        return mSp.getBoolean(
            "is_night_model",
            isSystemNightMode
        )
    }


}