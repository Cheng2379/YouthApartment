package com.cheng.youthapartment.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.activity.HomeActivity
import com.cheng.youthapartment.R
import com.cheng.youthapartment.activity.BrowseHistoryActivity
import com.cheng.youthapartment.activity.LoginActivity
import com.cheng.youthapartment.activity.MyAppointmentActivity
import com.cheng.youthapartment.activity.MyLeaseActivity
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.user.UserBean
import com.cheng.youthapartment.util.getYAParcelableExtra
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cheng.youthapartment.util.ViewUtil

/**
 *
 * @author Cheng
 * @since 2025/1/4
 */
class UserCenterFragment : Fragment() {
    private lateinit var view: View
    private val mActivity by lazy { requireActivity() as HomeActivity }
    private val mAvatarImg: ImageView by lazy { view.findViewById(R.id.user_avatar) }
    private val mUserName: TextView by lazy { view.findViewById(R.id.user_name) }
    private val mLogout: Button by lazy { view.findViewById(R.id.user_center_exit_login) }

    private val mSwitchThemeView: ImageView by lazy { view.findViewById(R.id.switch_theme_model) }
    private var mUserBean: UserBean? = null

    private val mSp = App.getSharedPreferences()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_user_center, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        onClickListener()
    }

    private fun initView() {
        // 根据主题状态显示对应的图标
        mSwitchThemeView.background = ResourcesCompat.getDrawable(
            resources,
            if (ViewUtil.isNightModel()) R.drawable.svg_light else R.drawable.svg_dark,
            null
        )

        mActivity.intent.getYAParcelableExtra<UserBean>("user")?.let {
            mUserBean = it
        } ?: run {
            mUserBean = UserBean(
                mSp.getString("nickname", ""),
                mSp.getString("avatarUrl", null)
            )
        }
        val hasValidAvatarUrl = mUserBean?.avatarUrl?.isNotEmpty() == true

        if (hasValidAvatarUrl) {
            Glide.with(this)
                .load(mUserBean?.avatarUrl)
                .error(R.drawable.img_user_center)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        mAvatarImg.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.icon_or_text,
                                null
                            )
                        )
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        mAvatarImg.backgroundTintList = null
                        return false
                    }
                })
                .apply(RequestOptions.bitmapTransform(SquareCrop(20)))
                .into(mAvatarImg)
        }
        mUserName.text = mUserBean?.nickname ?: "用户-xxxx"
    }

    private fun onClickListener() {
        // 租约历史
        view.findViewById<LinearLayout>(R.id.user_center_browse_history).setOnClickListener {
            startActivity(Intent(mActivity, BrowseHistoryActivity::class.java))
        }
        // 我的预约
        view.findViewById<LinearLayout>(R.id.user_center_my_reserve).setOnClickListener {
            startActivity(Intent(mActivity, MyAppointmentActivity::class.java))
        }
        // 我的租约
        view.findViewById<LinearLayout>(R.id.user_center_my_lease).setOnClickListener {
            startActivity(Intent(mActivity, MyLeaseActivity::class.java))
        }
        // 退出登录
        mLogout.setOnClickListener {
            AlertDialog.Builder(mActivity)
                .setMessage("是否确认退出登录")
                .setNegativeButton("否") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("是") { dialogInterface, position ->
                    startActivity(Intent(mActivity, LoginActivity::class.java))
                    mActivity.finish()
                    App.clearUserInfo()
                    dialogInterface.dismiss()
                }
                .show()
        }

        // 切换主题
        mSwitchThemeView.setOnClickListener {
            ViewUtil.setThemeModel(mSwitchThemeView, mActivity)
        }
    }


}