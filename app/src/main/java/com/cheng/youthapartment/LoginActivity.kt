package com.cheng.youthapartment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import com.cheng.youthapartment.util.OkHttpUtil
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import androidx.lifecycle.lifecycleScope
import com.cheng.youthapartment.bean.BaseBean
import com.cheng.youthapartment.util.showToast
import kotlinx.coroutines.delay

/**
 * 登录页面
 * @author Cheng
 * @since 2024/12/10
 */
class LoginActivity : BaseActivity() {
    private val phone: EditText by lazy { findViewById(R.id.phone) }
    private val captcha: EditText by lazy { findViewById(R.id.captcha) }
    private val hidePhoneText: TextView by lazy { findViewById(R.id.hidePhoneText) }
    private val hideCaptchaText: TextView by lazy { findViewById(R.id.hideCaptchaText) }
    private val sendCaptcha: Button by lazy { findViewById(R.id.sendCaptcha) }
    private val login: Button by lazy { findViewById(R.id.login) }
    private var phoneStr: String? = null
    private var captchaStr: String? = null
    private val phoneFormat: String =
        "^(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57]|19[89]|166)[0-9]{8}"
    private val TAG: String = javaClass.name.split(".").last()
    private val gson: Gson = Gson()
    private var token: String = ""
    //private val testToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSX0lORk8iLCJleHAiOjE3NjMxMzk1MDcsInVzZXJJZCI6MiwidXNlcm5hbWUiOiIxNTY3OTIxNjE2MiJ9" +
    //            ".mXZvDp-73_natBlclYEDSfjBtMqz9iwNsl5wCmzDCmE";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        editTextListener()
        sendCaptcha()
        login()
    }

    private fun editTextListener() {
        phoneStr = phone.text.toString()
        captchaStr = captcha.getText().toString()

        phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                phoneStr = charSequence.toString()
                if (!phoneCheck()) {
                    hidePhoneText.visibility = TextView.VISIBLE
                } else {
                    hidePhoneText.visibility = TextView.GONE
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        captcha.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                captchaStr = charSequence.toString()
                if (captchaStr!!.isEmpty()) {
                    hideCaptchaText.visibility = TextView.VISIBLE
                } else {
                    hideCaptchaText.visibility = TextView.GONE
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
    }

    /**
     * 号码校验
     */
    fun phoneCheck(): Boolean {
        if (phoneStr!!.isEmpty()) {
            return false
        } else {
            val pattern = Pattern.compile(phoneFormat)
            val matcher = phoneStr?.let {
                pattern.matcher(it)
            }
            return matcher?.matches()!!
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sendCaptcha() {
        sendCaptcha.setOnClickListener { v: View? ->
            if (phoneCheck()) {
                OkHttpUtil.get("/app/login/getCode?phone=$phoneStr") { _, response ->
                    val baseBean = gson.fromJson(
                        response,
                        BaseBean::class.java
                    )
                    lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            if (baseBean.code == 200) {
                                "发送成功".showToast(applicationContext)
                                sendCaptcha.text = "已发送"
                                sendCaptcha.isEnabled = false

                                // 倒计时
                                var remainingTime = 60
                                while (remainingTime > 0) {
                                    sendCaptcha.text = "$remainingTime 秒后可重新获取"
                                    sendCaptcha.textSize = 12f
                                    delay(1000)
                                    remainingTime--
                                }

                                sendCaptcha.isEnabled = true
                                sendCaptcha.text = "发送验证码"
                            } else {
                                baseBean.message?.showToast(this@LoginActivity)
                            }
                        }
                    }
                }
            } else {
                "请输入正确手机号".showToast(this)
            }
        }
    }

    private fun login() {
        login.setOnClickListener {
            if (phoneCheck() && captchaStr?.isNotEmpty() != false) {
                val map = mapOf<String, Any>("phone" to phoneStr!!, "code" to captchaStr!!)
                OkHttpUtil.post("/app/login", params = map) { _, response ->
                    response?.let {
                        val bodyBean = gson.fromJson(it, BaseBean::class.java)
                        if (bodyBean.code == 200) {
                            // 存储token，后续进行加密
                            token = bodyBean.data.toString()

                            getSharedPreferences("user_info", MODE_PRIVATE).edit {
                                putString("token", token)
                            }
                            // 获取用户信息
                            lifecycleScope.launch {
                                withContext(Dispatchers.Main) {
                                    val userBean = getLoginUserInfo(token, gson)
                                    userBean?.let {
                                        "登录成功".showToast(applicationContext)
                                        getSharedPreferences("user_info", MODE_PRIVATE).edit {
                                            putString("nickname", it.nickname)
                                            putString("avatarUrl", it.avatarUrl)
                                        }

                                        val intent =
                                            Intent(this@LoginActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                        // 销毁页面
                                        finish()
                                    }
                                }
                            }
                        } else {
                            lifecycleScope.launch(Dispatchers.Main) {
                                bodyBean.message?.showToast(this@LoginActivity)
                            }
                        }
                    }
                }
            } else {
                "请输入正确的手机号或验证码".showToast(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}