package com.cheng.youthapartment

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.cheng.youthapartment.util.Logger

/**
 *
 * @author Cheng
 * @since 2025/1/6
 */
class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context

        private val mSharedPreferences: SharedPreferences by lazy {
            if (!::mContext.isInitialized) {
                throw IllegalStateException("Context must be initialized before accessing SharedPreferences")
            }
            mContext.getSharedPreferences("user_info", MODE_PRIVATE)
        }

        fun getSharedPreferences() = mSharedPreferences

        fun clearUserInfo() {
            mSharedPreferences.edit {
                clear()
                Logger.i("SharePreferences Data cleared!")
            }
        }

        fun getToken(): String {
            return mSharedPreferences?.getString("token", "") ?: ""
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        Logger.init(Logger.DEBUG)
        Logger.setLogDeep(1)
    }
}