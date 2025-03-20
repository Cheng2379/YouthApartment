package com.cheng.youthapartment.fragment

import android.content.Intent
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
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.getYAParcelableExtra

/**
 *
 * @author Cheng
 * @since 2025/1/4
 */
class UserCenterFragment : Fragment() {
    private val activity by lazy { requireActivity() as HomeActivity }
    private val mAvatarImg: ImageView by lazy { view.findViewById(R.id.user_avatar) }
    private val mUserName: TextView by lazy { view.findViewById(R.id.user_name) }
    private val mLogout: Button by lazy { view.findViewById(R.id.exit_login) }
    private var userBean: UserBean? = null
    private lateinit var view: View

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
        activity.intent.getYAParcelableExtra<UserBean>("user")?.let {
            userBean = it
        } ?: run {
            userBean = UserBean(
                App.getSharedPreferences()?.getString("nickname", ""),
                App.getSharedPreferences()?.getString("avatarUrl", null)
            )
        }
        Logger.d("userBean: $userBean")
        userBean?.avatarUrl?.takeIf { it.isNotEmpty() }.let {
            Glide.with(this)
                .load(it)
                .error(R.drawable.img_user_center)
                .apply(
                    RequestOptions.bitmapTransform(SquareCrop(20))
                )
                .into(mAvatarImg)
        }
        mUserName.text = userBean?.nickname ?: "用户-xxxx"
    }

    private fun onClickListener() {
        // 租约历史
        view.findViewById<LinearLayout>(R.id.browse_history).setOnClickListener {
            startActivity(Intent(activity, BrowseHistoryActivity::class.java))
        }
        // 我的预约
        view.findViewById<LinearLayout>(R.id.my_reserve).setOnClickListener {
            startActivity(Intent(activity, MyAppointmentActivity::class.java))
        }
        // 我的租约
        view.findViewById<LinearLayout>(R.id.my_lease).setOnClickListener {
            startActivity(Intent(activity, MyLeaseActivity::class.java))
        }
        // 退出登录
        mLogout.setOnClickListener {
            AlertDialog.Builder(activity)
                .setMessage("是否确认退出登录")
                .setNegativeButton("否") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("是") { dialogInterface, position ->
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity.finish()
                    App.clearUserInfo()
                    dialogInterface.dismiss()
                }
                .show()
        }
    }


}