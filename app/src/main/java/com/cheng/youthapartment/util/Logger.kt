package com.cheng.youthapartment.util

import android.util.Log

/**
 *
 * @author Cheng
 * @since 2025/1/9
 */
object Logger {
    private const val TAG = "Lease"
    const val VERBOSE = 2
    const val DEBUG = 3
    const val INFO = 4
    const val WARN = 5
    const val ERROR = 6
    const val NOTHING = Int.MAX_VALUE
    private var logLevel = VERBOSE
    private var stackDeep = 1

    fun init(level: Int = VERBOSE) {
        logLevel = level
    }

    /**
     * 日志深度
     */
    fun setLogDeep(deep: Int) {
        stackDeep = if (deep >= 7) 7 else deep
    }

    fun v(msg: String) = v(TAG, msg)

    fun d(msg: String) = d(TAG, msg)

    fun i(msg: String) = i(TAG, msg)

    fun w(msg: String) = w(TAG, msg)

    fun e(msg: String) = e(TAG, msg)

    fun v(tag: String, msg: String) {
        if (logLevel <= VERBOSE) {
            Log.v(tag, formatMSG(msg))
        }
    }

    fun d(tag: String, msg: String) {
        if (logLevel <= DEBUG) {
            Log.d(tag, formatMSG(msg))
        }
    }

    fun i(tag: String, msg: String) {
        if (logLevel <= INFO) {
            Log.i(tag, formatMSG(msg))
        }
    }

    fun w(tag: String, msg: String) {
        if (logLevel <= WARN) {
            Log.w(tag, formatMSG(msg))
        }
    }

    fun e(tag: String, msg: String) {
        if (logLevel <= ERROR) {
            Log.e(tag, formatMSG(msg))
        }
    }

    private fun formatMSG(msg: String): String {
        val stackTrace = Throwable().stackTrace
        val extendMSG = StringBuilder()
        //stackTrace.forEachIndexed { index, stackTraceElement ->
        //    Log.d("StackTrace", "$index: $stackTraceElement")
        //}
        /**
         * 日志深度从3开始，到深度+3结束
         * 从formatMSG()方法往外数，formatMSG()->Log.d()->Logger.d()->Logger.d()->调用处
         * 第5层就是调用Logger.d()的位置
         */
        for (i in 3 until stackDeep + 3) {
            if (i < stackTrace.size) {
                stackTrace[i]?.let {
                    extendMSG.append(
                        "${it.className.split(".").last()}#${it.methodName}()#${it.lineNumber}"
                    )
                }
            } else {
                // 超出堆栈大小，退出循环
                break
            }
        }
        return "$extendMSG——>$msg"
    }
}