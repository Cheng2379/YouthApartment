package com.cheng.youthapartment

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.cheng.youthapartment.util.Logger

/**
 *
 * @author Cheng
 * @since 2025/1/6
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        mSharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE)
        Logger.init(Logger.DEBUG)
        Logger.setLogDeep(1)
    }

    @SuppressLint("StaticFieldLeak")
    companion object {
        lateinit var mContext: Context
        private var mSharedPreferences: SharedPreferences? = null

        fun getToken(): String {
            return mSharedPreferences?.getString("token", "")!!
        }

        fun clearUserInfo() {
            mSharedPreferences?.edit()?.clear()?.apply()
        }
    }
}