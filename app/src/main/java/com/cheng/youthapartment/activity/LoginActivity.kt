package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.cheng.youthapartment.R
import com.cheng.youthapartment.bean.BaseBean
import com.cheng.youthapartment.util.DataUtil
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.showToast
import com.cheng.youthapartment.util.textChangedListener
import kotlinx.coroutines.delay

/**
 * 登录页面
 * TODO: 底部添加隐私政策按钮，点击弹出弹窗，显示隐私政策
 * TODO: 调用代码参考<a href="https://blog.csdn.net/rain67/article/details/132174955">
 * @author Cheng
 * @since 2024/12/10
 */
class LoginActivity : BaseActivity() {
    private val mPhone: EditText by lazy { findViewById(R.id.login_edit_phone) }
    private val mCaptcha: EditText by lazy { findViewById(R.id.login_edit_captcha) }
    private val mHidePhoneText: TextView by lazy { findViewById(R.id.login_hide_phone_wrong_text) }
    private val mHideCaptchaText: TextView by lazy { findViewById(R.id.login_hide_captcha_wrong_text) }
    private val mSendCaptcha: Button by lazy { findViewById(R.id.login_send_captcha) }
    private val mLogin: Button by lazy { findViewById(R.id.login_btn) }
    private val mCheckBox:CheckBox by lazy { findViewById(R.id.login_cb) }
    private val mPrivacyPolicy: TextView by lazy { findViewById(R.id.login_privacy_policy) }

    private var mIsChecked = false
    private var mPhoneStr: String = ""
    private var mCaptchaStr: String = ""
    private var mToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
        sendCaptcha()
        login()
    }

    private fun initView() {
        mPhoneStr = mPhone.text.toString()
        mCaptchaStr = mCaptcha.getText().toString()

        mPhone.textChangedListener { charSequence, start, before, count ->
            mPhoneStr = charSequence.toString()
            if (!DataUtil.checkPhone(mPhoneStr)) {
                mHidePhoneText.visibility = TextView.VISIBLE
            } else {
                mHidePhoneText.visibility = TextView.GONE
            }
        }

        mCaptcha.textChangedListener { charSequence, start, before, count ->
            mCaptchaStr = charSequence.toString()
            if (mCaptchaStr.isEmpty()) {
                mHideCaptchaText.visibility = TextView.VISIBLE
            } else {
                mHideCaptchaText.visibility = TextView.GONE
            }
        }

        mCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            mIsChecked = isChecked
        }


    }

    fun wrongHint() {
        mHidePhoneText.visibility = if (mPhoneStr.isEmpty()) TextView.VISIBLE else TextView.GONE
        mHideCaptchaText.visibility = if (mCaptchaStr.isEmpty()) TextView.VISIBLE else TextView.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun sendCaptcha() {
        mSendCaptcha.setOnClickListener {
            if (DataUtil.checkPhone(mPhoneStr)) {
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
            wrongHint()
            if (DataUtil.checkPhone(mPhoneStr) && mCaptchaStr.isNotEmpty()) {
                val map = mapOf<String, Any>("phone" to mPhoneStr, "code" to mCaptchaStr)
                RetrofitUtil.post<BaseBean<Any>>("/app/login", params = map) { _, response ->
                    response?.let {
                        if (it.code == 200) {
                            // 存储token，后续进行加密
                            mToken = it.data.toString()

                            // 获取用户信息
                            lifecycleScope.launch(Dispatchers.Main) {
                                val userBean = getLoginUserInfo(mToken)
                                Logger.d("userBean: $userBean")
                                userBean?.let { bean ->
                                    "登录成功".showToast()
                                    getSharedPreferences("user_info", MODE_PRIVATE).edit {
                                        putString("token", mToken)
                                        putString("nickname", bean.nickname)
                                        putString("avatarUrl", bean.avatarUrl ?: "")
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