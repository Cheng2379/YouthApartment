package com.cheng.youthapartment.util

import android.util.Log
import com.cheng.youthapartment.listener.TimingEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionPool
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

/**
 *
 * @author Cheng
 * @since 2024/12/11
 */
object OkHttpUtil {
    private const val TAG: String = "OkHttpUtil"
    private const val BASE_URL: String = "http://127.0.0.1:8082"
    private var client: OkHttpClient

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            //.eventListener(TimingEventListener())
            .build()
    }

    fun get(
        suffixUrl: String,
        header: String? = null,
        params: Map<String, Any>? = null,
        callBack: (call: Call?, response: String?) -> Unit
    ) {
        request(HttpMethod.GET, suffixUrl, header, params, callBack)
    }

    fun post(
        suffixUrl: String,
        header: String? = null,
        params: Map<String, Any>? = null,
        callBack: (call: Call?, response: String?) -> Unit
    ) {
        request(HttpMethod.POST, suffixUrl, header, params, callBack)
    }

    private fun buildRequest(
        httpMethod: HttpMethod,
        url: String,
        header: String? = null,
        params: Map<String, Any>? = null,
    ): Request {
        val requestBuilder = Request.Builder().url(url)
        if (!header.isNullOrEmpty()) {
            requestBuilder.header("access-token", header)
        }
        when (httpMethod) {
            HttpMethod.GET -> {
                requestBuilder.get()
            }

            HttpMethod.POST -> {
                val json = params?.let { JSONObject(it).toString() } ?: "{}"
                val requestBody =
                    json.toRequestBody("application/json;charset=utf8".toMediaTypeOrNull())
                requestBuilder.post(requestBody)
            }
        }
        return requestBuilder.build()
    }

    private inline fun request(
        httpMethod: HttpMethod,
        suffixUrl: String,
        header: String? = null,
        params: Map<String, Any>? = null,
        crossinline callBack: (call: Call?, response: String?) -> Unit
    ) {
        val url = buildUrl(suffixUrl, params)
        val request = buildRequest(httpMethod, url, header, params)

        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "request fail -> ${e.message}", e)
                    callBack(call, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string() ?: ""
                    callBack(call, result)
                }
            })
        } catch (e: IOException) {
            Log.e(TAG, "request error -> ${e.message}", e)
        }
    }

    private fun buildUrl(
        suffixUrl: String,
        params: Map<String, Any>? = null,
    ): String {
        val suffixUrlBuilder = StringBuilder(suffixUrl)
        if (!params.isNullOrEmpty()) {
            suffixUrlBuilder.append("?")
            for ((key, value) in params) {
                suffixUrlBuilder.append(key).append("=").append(value.toString()).append("&")
            }
            suffixUrlBuilder.deleteCharAt(suffixUrlBuilder.length - 1)
        }
        return BASE_URL + suffixUrlBuilder.toString()
    }

    enum class HttpMethod {
        GET,
        POST
    }

}