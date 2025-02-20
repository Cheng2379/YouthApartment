package com.cheng.youthapartment.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cheng.youthapartment.bean.user.UserBean
import com.cheng.youthapartment.util.RetrofitUtil
import com.google.gson.Gson
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    /**
     * 获取登录信息
     */
    suspend fun getLoginUserInfo(token: String?, gson: Gson): UserBean? {
        return suspendCoroutine { coroutine ->
            RetrofitUtil.get<UserBean>("/app/info", token) { _, response ->
                response?.let {
                    coroutine.resume(it)
                } ?: coroutine.resume(null)
            }
        }
    }
}





