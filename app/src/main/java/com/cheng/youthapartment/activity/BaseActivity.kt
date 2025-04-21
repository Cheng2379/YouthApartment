package com.cheng.youthapartment.activity

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.cheng.youthapartment.App
import com.cheng.youthapartment.entity.user.UserBean
import com.cheng.youthapartment.listener.ThemeModelChangeListener
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.manager.ThemeModelManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 基础页，用于管理所有页面的状态
 * 所有Activity必须继承BaseActivity
 */
open class BaseActivity : AppCompatActivity(), ThemeModelChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ActivityCollector.addActivity(this)
        ThemeModelManager.registerThemeModelListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        ActivityCollector.removeActivity(this)
        ThemeModelManager.unregisterThemeModelListener(this)
    }

    /**
     * 销毁指定Activity
     */
    fun destroyActivity(activity: Activity) {
        ActivityCollector.removeActivity(activity)
    }

    /**
     * 获取登录信息
     */
    suspend fun getLoginUserInfo(token: String): UserBean? {
        return suspendCoroutine { coroutine ->
            RetrofitUtil.get<UserBean>("/app/info", token) { _, response ->
                response?.let {
                    coroutine.resume(it)
                } ?: coroutine.resume(null)
            }
        }
    }

    override fun onThemeModelChanged(isNightModel: Boolean) {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        /**
         * 将uiMode与UI_MODE_NIGHT_MASK进行与操作，提取出uiMode中与夜间模式相关的部分
         * newConfig.uiMode: 是一个整数，表示当前的UI模式。
         * Configuration.UI_MODE_NIGHT_MASK: 是一个掩码，用于提取UI模式中的夜间模式信息。
         */
        val currentMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = currentMode == Configuration.UI_MODE_NIGHT_YES
        ThemeModelManager.notifyThemeModelChanged(isNightMode)

        App.getSharedPreferences().edit {
            putBoolean("is_night_model", isNightMode)
        }
    }
}





