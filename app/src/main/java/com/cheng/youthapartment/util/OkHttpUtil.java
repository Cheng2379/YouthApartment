package com.cheng.youthapartment.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtil {
    private static final String TAG = "OkHttpUtils";
    private static final String BASE_URL = "http://106.55.104.120:8082";
    private static OkHttpUtil instance = new OkHttpUtil();
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static OkHttpClient client;

    public OkHttpUtil() {
        // 添加日志，设置日志输出等级
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpUtil getInstance() {
        return instance;
    }

    public void get(String suffixUrl, String header, Callback callback, Context context) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL.concat(suffixUrl))
                .get();
        if (header != null && !header.isEmpty()) {
            requestBuilder.header("access-token", header);
        }
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        enqueue(callback, call, context);
    }

    public void get(String suffixUrl, String header, ICallBack callback, Context context) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL.concat(suffixUrl))
                .get();
        if (header != null && !header.isEmpty()) {
            requestBuilder.header("access-token", header);
        }
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        enqueue(callback, call, context);
    }

    public void get(String suffixUrl, String header, Map<String, Object> params, ICallBack callback, Context context) {
        StringBuilder suffixUrlBuilder = new StringBuilder(suffixUrl);
        if (params != null && !params.isEmpty()) {
            suffixUrlBuilder.append("?");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                suffixUrlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            suffixUrlBuilder.deleteCharAt(suffixUrlBuilder.length() - 1);
        }
        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL.concat(suffixUrlBuilder.toString()))
                .get();
        if (header != null && !header.isEmpty()) {
            requestBuilder.header("access-token", header);
        }
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        enqueue(callback, call, context);
    }

    public void post(String suffixUrl, String header, Map<String, Object> params, Callback callback, Context context) {
        String json = new JSONObject(params).toString();
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json;charset=utf8"));

        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL.concat(suffixUrl))
                .post(requestBody);
        if (header != null && !header.isEmpty()) {
            requestBuilder.header("access-token", header);
        }
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        enqueue(callback, call, context);
    }

    public void post(String suffixUrl, String header, Map<String, Object> params, ICallBack callback, Context context) {
        String json = new JSONObject(params).toString();
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json;charset=utf8"));

        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL.concat(suffixUrl))
                .post(requestBody);
        if (header != null && !header.isEmpty()) {
            requestBuilder.header("access-token", header);
        }
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        enqueue(callback, call, context);
    }

    private void enqueue(Callback callBack, Call call, Context context) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.post(() -> {
                    Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                handler.post(() -> {
                    try {
                        callBack.onResponse(call, response);
                    } catch (IOException e) {
                        callBack.onFailure(call, e);
                    }
                });
            }
        });
    }

    private void enqueue(ICallBack callBack, Call call, Context context) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.post(() -> {
                    Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                handler.post(() -> {
                    try {
                        if (response.isSuccessful()) {
                            callBack.onSuccess(call, response.body().string());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Response failed: " + e.getMessage());
                    }
                });
            }
        });
    }

    public interface ICallBack {
        void onSuccess(Call call, String response);
    }
}
