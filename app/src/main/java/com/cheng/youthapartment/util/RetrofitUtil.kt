package com.cheng.youthapartment.util

import android.util.Log
import com.cheng.youthapartment.bean.BaseBean
import com.cheng.youthapartment.listener.TimingEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

/**
 *
 * @author Cheng
 * @since 2024/12/24
 */
object RetrofitUtil {
    private const val BASE_URL: String = "http://106.55.104.120:8082"
    const val TAG: String = "RetrofitUtil"
    var apiService: ApiService
    val gson = Gson()

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            //.eventListener(TimingEventListener())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    inline fun <reified T> get(
        suffixUrl: String,
        headers: String? = null,
        params: Map<String, Any>? = null,
        noinline callBack: (call: Call<ResponseBody>?, response: T?) -> Unit
    ) {
        execute(HttpMethod.GET, suffixUrl, headers, params, callBack)
    }


    inline fun <reified T> post(
        suffixUrl: String,
        headers: String? = null,
        params: Map<String, Any>? = null,
        noinline callBack: (call: Call<ResponseBody>?, response: T?) -> Unit
    ) {
        execute(HttpMethod.POST, suffixUrl, headers, params, callBack)
    }

    inline fun <reified T> execute(
        httpMethod: HttpMethod,
        suffixUrl: String,
        headers: String? = null,
        params: Map<String, Any>? = null,
        noinline callBack: (call: Call<ResponseBody>?, response: T?) -> Unit
    ) {
        val subParams = params ?: emptyMap()
        val call: Call<ResponseBody> = when (httpMethod) {
            HttpMethod.GET -> {
                apiService.get(suffixUrl, headers, subParams)
            }

            HttpMethod.POST -> {
                apiService.post(suffixUrl, headers, subParams)
            }
        }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()?.string() ?: ""
                    try {
                        if (T::class == BaseBean::class) {
                            val parsedResponse =
                                gson.fromJson<BaseBean<Any>>(result, object : TypeToken<BaseBean<Any>>() {}.type)
                            callBack(call, parsedResponse as? T)
                        } else {
                            val parsedResponse =
                                gson.fromJson(result, object : TypeToken<BaseBean<T>>() {})
                            if (parsedResponse.code == 200) {
                                callBack(call, parsedResponse.data)
                            } else {
                                Log.d(TAG, "response code: $result")
                                callBack(call, null)
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "GSON解析失败 -> $result \nexception -> ${e.printStackTrace()}")
                        callBack(call, null)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, response: Throwable) {
                Log.e(TAG, "request fail -> ${response.message}", response)
                callBack(call, null)
            }
        })
    }


    enum class HttpMethod {
        GET,
        POST
    }

    interface ApiService {
        @GET
        fun get(
            @Url suffixUrl: String,
            @Header("access-token") headers: String? = null,
            @QueryMap params: Map<String, @JvmSuppressWildcards Any>? = null,
        ): Call<ResponseBody>

        @POST
        fun post(
            @Url suffixUrl: String,
            @Header("access-token") headers: String? = null,
            @Body params: Map<String, @JvmSuppressWildcards Any>? = null,
        ): Call<ResponseBody>
    }
}