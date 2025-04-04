package com.cheng.youthapartment.util

import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.cheng.youthapartment.App
import com.cheng.youthapartment.activity.BaseActivity

/**
 * 操作视图工具类
 *
 * @author CHENG
 * @since 2025/4/5
 */
object ViewUtil {
    private val mSp = App.getSharedPreferences()
    private var mIsDark = false

    /**
     * 设置主题
     */
    fun setThemeModel(view: View, activity: BaseActivity) {
        mIsDark = isNightModel()
        view.animate().rotationBy(180f)
            .setDuration(500)
            .withEndAction {
                // 等待动画结束后再触发activity重建
                activity.recreate()
            }
            .start()

        val newMode = if (mIsDark) AppCompatDelegate.MODE_NIGHT_NO
        else AppCompatDelegate.MODE_NIGHT_YES
        App.getSharedPreferences().edit {
            putInt("night_model", newMode)
        }
        AppCompatDelegate.setDefaultNightMode(newMode)
        mIsDark = !mIsDark
    }

    /**
     * 获取当前主题
     * TODO 后续改为监听系统模式，而不是自定义的参数
     * @return 是否为黑暗模式
     */
    fun isNightModel(): Boolean {
        return mSp.getInt("night_model", AppCompatDelegate.getDefaultNightMode()) ==
                AppCompatDelegate.MODE_NIGHT_YES
    }
}