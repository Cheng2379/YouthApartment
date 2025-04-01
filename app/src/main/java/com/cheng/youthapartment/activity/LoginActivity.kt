package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.amap.api.maps.MapsInitializer
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.bean.BaseBean
import com.cheng.youthapartment.util.DataUtil
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.showToast
import com.cheng.youthapartment.util.textChangedListener
import com.cheng.youthapartment.util.toHtml
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 登录页面
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
    private val mCheckBox: CheckBox by lazy { findViewById(R.id.login_cb) }
    private val mPrivacyPolicy: TextView by lazy { findViewById(R.id.login_privacy_policy) }

    private var lastUpdateDate: String? = null
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

        mCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mIsChecked = isChecked
        }

        // 处理隐私政策点击事件
        val privacyPolicyText = mPrivacyPolicy.text.toString()
        val spannableString = SpannableString(privacyPolicyText)

        val readAndAgree = "已阅读并同意"
        val serviceAgreement = "服务协议"
        val privacyPolicy = "隐私保护政策"

        val readAndAgreeStartIndex = privacyPolicyText.indexOf(readAndAgree)
        val readAndAgreeEndIndex = readAndAgreeStartIndex + readAndAgree.length

        val serviceStartIndex = privacyPolicyText.indexOf(serviceAgreement)
        val serviceEndIndex = serviceStartIndex + serviceAgreement.length

        val privacyPolicyStartIndex = privacyPolicyText.indexOf(privacyPolicy)
        val privacyPolicyEndIndex = privacyPolicyStartIndex + privacyPolicy.length

        // 点击前半部分勾选同意
        setClickableSpan(
            0,
            readAndAgreeStartIndex,
            readAndAgreeEndIndex,
            spannableString
        )
        // 设置服务协议点击事件
        setClickableSpan(
            1,
            serviceStartIndex,
            serviceEndIndex,
            spannableString
        )
        // 设置隐私保护政策点击事件
        setClickableSpan(
            2,
            privacyPolicyStartIndex,
            privacyPolicyEndIndex,
            spannableString
        )

        mPrivacyPolicy.text = spannableString
        mPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
        mPrivacyPolicy.highlightColor = Color.TRANSPARENT

        mPrivacyPolicy.setOnClickListener(null)
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
                if (!mIsChecked) {
                    """请勾选"用户协议与隐私政策"""".showToast()
                    return@setOnClickListener
                }
                // 设置高德隐私合规性
                setPrivacyCompliance()
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
                                    App.getSharedPreferences().edit {
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
                            response.message?.showToast()
                        }
                    }
                }
            } else {
                "请输入正确的手机号或验证码".showToast()
            }
        }
    }

    /**
     * 0: 同意服务协议
     * 1: 服务协议高亮与点击事件
     * 2: 隐私保护政策高亮与点击事件
     */
    private fun setClickableSpan(
        textType: Int,
        textStartIndex: Int,
        textEndIndex: Int,
        spannableString: SpannableString
    ) {
        if (textStartIndex > 0) {
            val span = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    when (textType) {
                        0 -> {
                            if (mIsChecked) {
                                mCheckBox.isChecked = false
                                mIsChecked = false
                            } else {
                                mCheckBox.isChecked = true
                                mIsChecked = true
                            }
                        }

                        1, 2 -> {
                            setDialog(textType)
                        }
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    if (textType != 0) {
                        // 集成该父类方法会使高亮文本失效，在此处让"已阅读并同意"这几个字高亮效果失效
                        super.updateDrawState(ds)
                    }
                    ds.isUnderlineText = false
                }
            }
            spannableString.setSpan(
                span,
                textStartIndex,
                textEndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    /**
     * 根据服务协议或隐私政策显示不同的信息
     */
    private fun setDialog(textType: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_privacy, null)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.setCanceledOnTouchOutside(false)
        // 设置返回不关闭对话框
        //dialog.setCancelable(false)

        val titleView: TextView = view.findViewById(R.id.privacy_policy_title)
        val lastUpdateDateView: TextView = view.findViewById(R.id.privacy_policy_last_update_date)
        val contentTextView: TextView = view.findViewById(R.id.privacy_policy_content)

        val closeBtn: Button = view.findViewById(R.id.close_btn)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        lastUpdateDate = simpleDateFormat.format(Date(System.currentTimeMillis()))


        when (textType) {
            1 -> {
                lastUpdateDateView.text = this.getText(R.string.service_last_update_date).toHtml()
                titleView.text = this.getText(R.string.terms_of_service_title).toHtml()
                contentTextView.text = this.getText(R.string.terms_of_service_content).toHtml()
            }

            2 -> {
                lastUpdateDateView.text = this.getText(R.string.privacy_policy_last_update_date).toHtml()
                titleView.text = this.getText(R.string.privacy_policy_title).toHtml()
                contentTextView.text = this.getText(R.string.privacy_policy_content).toHtml()
            }
        }

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * 使用地图功能之前，必须设置隐私合规
     */
    private fun setPrivacyCompliance() {
        // 设置高德SDK合规性，若不设置该属性，无法正常使用高德SDK
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);
    }

}