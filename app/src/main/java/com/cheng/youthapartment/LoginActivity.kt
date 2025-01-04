package com.cheng.youthapartment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.cheng.youthapartment.bean.BaseBean;
import com.cheng.youthapartment.bean.UserBean;
import com.cheng.youthapartment.util.OkHttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText phone, captcha;
    private TextView hidePhoneText, hideCaptchaText;
    private Button sendCaptcha, login;
    private String phoneStr, captchaStr;
    private SharedPreferences spf;
    private SharedPreferences.Editor spEditor;
    private static final String phoneFormat = "^(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57]|19[89]|166)[0-9]{8}";
    private static final String TAG = "Login";
    private static final Gson gson = new Gson();
    //    private static final String longTimeToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSX0lORk8iLCJleHAiOjE3NjMxMzk1MDcsInVzZXJJZCI6MiwidXNlcm5hbWUiOiIxNTY3OTIxNjE2MiJ9" +
//            ".mXZvDp-73_natBlclYEDSfjBtMqz9iwNsl5wCmzDCmE";
    private static Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        spf = this.getSharedPreferences("user_info", MODE_PRIVATE);
        initView();
        editTextListener();//输入框监听
        sendCaptcha();
        login();
    }

    private void initView() {
        phone = findViewById(R.id.phone);
        captcha = findViewById(R.id.captcha);
        hidePhoneText = findViewById(R.id.hidePhoneText);
        hideCaptchaText = findViewById(R.id.hideCaptchaText);
        sendCaptcha = findViewById(R.id.sendCaptcha);
        login = findViewById(R.id.login);
    }

    private void editTextListener() {
        phoneStr = phone.getText().toString();
        captchaStr = captcha.getText().toString();

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                phoneStr = charSequence.toString();
                if (!phoneCheck()) {
                    hidePhoneText.setVisibility(TextView.VISIBLE);
                } else {
                    hidePhoneText.setVisibility(TextView.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        captcha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                captchaStr = charSequence.toString();
                if (captchaStr.isEmpty()) {
                    hideCaptchaText.setVisibility(TextView.VISIBLE);
                } else {
                    hideCaptchaText.setVisibility(TextView.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /**
     * 号码校验
     *
     * @return 是否通过校验
     */
    public boolean phoneCheck() {
        if (phoneStr.isEmpty()) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(phoneFormat);
            Matcher matcher = pattern.matcher(phoneStr);
            return matcher.matches();
        }
    }

    private void sendCaptcha() {
        sendCaptcha.setOnClickListener(v -> {
            if (phoneCheck()) {
//                OkHttpUtil.getInstance().get("/app/login/getCode?phone=" + phoneStr, null, new Callback() {
//                    @Override
//                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                        Log.i("CaptchaResponse", "ResCode" + e.getMessage());
//                        Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(@NonNull Call call, @NonNull Response response) {
//                        try {
//                            String body = response.body().string();
//
//                            ResponseBodyBean bodyBean = gson.fromJson(body, ResponseBodyBean.class);
//                            Log.i("CaptchaResponse", bodyBean.toString());
//
//                            if (bodyBean.getCode() == 200) {
//                                handler.post(() -> {
//                                    Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
//                                });
//                                sendCaptcha.setText("已发送");
//                                sendCaptcha.setEnabled(false);
//
//                                handler.postDelayed(() -> {
//                                    sendCaptcha.setEnabled(true);
//                                    sendCaptcha.setText("发送验证码");
//                                }, 60000);
//                            }
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }, this);
                OkHttpUtil.getInstance().get("/app/login/getCode?phone=" + phoneStr, null,
                        (call, response) -> {
                            BaseBean baseBean = gson.fromJson(response, BaseBean.class);
                            if (baseBean.getCode() == 200) {
                                Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();

                                sendCaptcha.setText("已发送");
                                sendCaptcha.setEnabled(false);

                                handler.postDelayed(() -> {
                                    sendCaptcha.setEnabled(true);
                                    sendCaptcha.setText("发送验证码");
                                }, 60000);
                            } else {
                                Toast.makeText(this, baseBean.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, this);
            } else {
                Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login() {
        login.setOnClickListener(v -> {
            if (phoneCheck() && !captchaStr.isEmpty()) {
                Map<String, Object> map = new HashMap<>();
                map.put("phone", phoneStr);
                map.put("code", captchaStr);

                OkHttpUtil.getInstance().post("/app/login", null, map,
                        (call, response) -> {
                            BaseBean bodyBean = gson.fromJson(response, BaseBean.class);
                            if (bodyBean.getCode() == 200) {
                                // 存储token，后续进行加密
                                String token = bodyBean.getData().toString();
                                spEditor = spf.edit();
                                spEditor.putString("token", token);
                                spEditor.apply();

                                // 获取用户信息
                                getLoginInfo(token);
                            } else {
                                Toast.makeText(getApplicationContext(), bodyBean.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        , this);
            } else {
                Toast.makeText(getApplicationContext(), "请输入正确的手机号或验证码", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLoginInfo(String token) {
        OkHttpUtil.getInstance().get("/app/info", token,
                (call, response) -> {
                    TypeToken<BaseBean<UserBean>> typeToken = new TypeToken<BaseBean<UserBean>>() {};
                    BaseBean<UserBean> baseBean = gson.fromJson(response, typeToken);

                    if (baseBean.getCode() == 200) {
                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        spEditor = spf.edit();
                        spEditor.putString("nickname", baseBean.getData().getNickname());
                        spEditor.putString("avatarUrl", baseBean.getData().getAvatarUrl());
                        spEditor.apply();

                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        // 销毁页面
                        finish();
                    }
                }, this);

    }

}