package com.cheng.youthapartment.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.activity.HomeActivity
import com.cheng.youthapartment.R
import com.cheng.youthapartment.activity.BrowseHistoryActivity
import com.cheng.youthapartment.activity.LoginActivity
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.user.UserBean

/**
 *
 * @author Cheng
 * @since 2025/1/4
 */
class UserCenterFragment : Fragment() {
    private val activity by lazy { requireActivity() as HomeActivity }
    private val mAvatarImg: ImageView by lazy { view.findViewById(R.id.user_avatar) }
    private val mUserName: TextView by lazy { view.findViewById(R.id.user_name) }
    private val mExitLogin: Button by lazy { view.findViewById(R.id.exit_login) }
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
        val userBean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.intent.getParcelableExtra("user", UserBean::class.java)
        } else {
            activity.intent.getParcelableExtra("user") as? UserBean
        }
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
        view.findViewById<LinearLayout>(R.id.browse_history).setOnClickListener {
            val intent = Intent(activity, BrowseHistoryActivity::class.java)
            intent.putExtra("token", activity.getSharedPreferences("user_info", MODE_PRIVATE).getString("token", ""))
            startActivity(intent)
        }
        view.findViewById<LinearLayout>(R.id.my_reserve).setOnClickListener {

        }
        view.findViewById<LinearLayout>(R.id.my_lease).setOnClickListener {

        }

        mExitLogin.setOnClickListener {
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