package com.cheng.youthapartment.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cheng.youthapartment.bean.user.UserBean
import com.cheng.youthapartment.util.RetrofitUtil
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 基础页，用于管理所有页面的状态
 * 所有Activity必须继承BaseActivity
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ActivityCollector.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

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
}





