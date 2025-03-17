package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.edit
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import androidx.lifecycle.lifecycleScope
import com.cheng.youthapartment.R
import com.cheng.youthapartment.bean.BaseBean
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.showToast
import kotlinx.coroutines.delay

/**
 * 登录页面
 * @author Cheng
 * @since 2024/12/10
 */
class LoginActivity : BaseActivity() {
    private val mPhone: EditText by lazy { findViewById(R.id.phone) }
    private val mCaptcha: EditText by lazy { findViewById(R.id.captcha) }
    private val mHidePhoneText: TextView by lazy { findViewById(R.id.hidePhoneText) }
    private val mHideCaptchaText: TextView by lazy { findViewById(R.id.hideCaptchaText) }
    private val mSendCaptcha: Button by lazy { findViewById(R.id.sendCaptcha) }
    private val mLogin: Button by lazy { findViewById(R.id.login_btn) }
    private var mPhoneStr: String? = null
    private var mCaptchaStr: String? = null
    private val mPhoneFormat: String =
        "^(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57]|19[89]|166)[0-9]{8}"
    private val mGson: Gson = Gson()
    private var mToken: String = ""
    //private val testToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSX0lORk8iLCJleHAiOjE3NjMxMzk1MDcsInVzZXJJZCI6MiwidXNlcm5hbWUiOiIxNTY3OTIxNjE2MiJ9" +
    //            ".mXZvDp-73_natBlclYEDSfjBtMqz9iwNsl5wCmzDCmE";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextListener()
        sendCaptcha()
        login()
    }

    private fun editTextListener() {
        mPhoneStr = mPhone.text.toString()
        mCaptchaStr = mCaptcha.getText().toString()

        mPhone.addTextChangedListener(object : TextWatcher {
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
                mPhoneStr = charSequence.toString()
                if (!phoneCheck()) {
                    mHidePhoneText.visibility = TextView.VISIBLE
                } else {
                    mHidePhoneText.visibility = TextView.GONE
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        mCaptcha.addTextChangedListener(object : TextWatcher {
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
                mCaptchaStr = charSequence.toString()
                if (mCaptchaStr!!.isEmpty()) {
                    mHideCaptchaText.visibility = TextView.VISIBLE
                } else {
                    mHideCaptchaText.visibility = TextView.GONE
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
        if (mPhoneStr!!.isEmpty()) {
            return false
        } else {
            val pattern = Pattern.compile(mPhoneFormat)
            val matcher = mPhoneStr?.let {
                pattern.matcher(it)
            }
            return matcher?.matches()!!
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sendCaptcha() {
        mSendCaptcha.setOnClickListener {
            if (phoneCheck()) {
                lifecycleScope.launch(Dispatchers.Main) {
                    mSendCaptcha.text = "已发送"
                    mSendCaptcha.isEnabled = false
                    // 倒计时
                    var remainingTime = 60
                    while (remainingTime > 0) {
                        mSendCaptcha.text = "$remainingTime 秒后可重新获取"
                        mSendCaptcha.textSize = 12f
                        delay(1000)
                        remainingTime--
                    }
                    mSendCaptcha.isEnabled = true
                    mSendCaptcha.text = "发送验证码"
                }
                RetrofitUtil.get<BaseBean<Any>>("/app/login/getCode?phone=$mPhoneStr") { _, response ->
                    response?.let {
                        if (it.code == 200) {
                            "发送成功".showToast()
                        } else {
                            it.message?.showToast()
                        }
                    }
                }
            } else {
                "请输入正确手机号".showToast()
            }
        }
    }

    private fun login() {
        mLogin.setOnClickListener {
            if (phoneCheck() && mCaptchaStr?.isNotEmpty() != false) {
                val map = mapOf<String, Any>("phone" to mPhoneStr!!, "code" to mCaptchaStr!!)
                RetrofitUtil.post<BaseBean<Any>>("/app/login", params = map) { _, response ->
                    response?.let {
                        if (response.code == 200) {
                            // 存储token，后续进行加密
                            mToken = response.data.toString()

                            // 获取用户信息
                            lifecycleScope.launch(Dispatchers.Main) {
                                val userBean = getLoginUserInfo(mToken)
                                Logger.d("userBean: $userBean")
                                userBean?.let {
                                    "登录成功".showToast()
                                    getSharedPreferences("user_info", MODE_PRIVATE).edit {
                                        putString("token", mToken)
                                        putString("nickname", it.nickname)
                                        putString("avatarUrl", it.avatarUrl ?: "")
                                    }

                                    val intent =
                                        Intent(this@LoginActivity, HomeActivity::class.java)
                                    intent.putExtra("user", userBean)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        } else {
                            lifecycleScope.launch(Dispatchers.Main) {
                                response.message?.showToast()
                            }
                        }
                    }
                }
            } else {
                "请输入正确的手机号或验证码".showToast()
            }
        }
    }
}