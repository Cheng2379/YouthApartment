package com.cheng.youthapartment.listener

import android.util.Log
import okhttp3.Call
import okhttp3.EventListener

/**
 *
 * @author Cheng
 * @since 2025/1/4
 */
class TimingEventListener : EventListener()  {
    private var startNs = 0L

    override fun callStart(call: Call) {
        startNs = System.nanoTime()
    }

    override fun callEnd(call: Call) {
        val timeMs = (System.nanoTime() - startNs) / 1000000
        Log.d("TimingEventListener", "Request Time spent: $timeMs ms")
    }
}