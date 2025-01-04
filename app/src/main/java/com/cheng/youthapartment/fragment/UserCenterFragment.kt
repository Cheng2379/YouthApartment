package com.cheng.youthapartment.fragment

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.cheng.youthapartment.HomeActivity
import com.cheng.youthapartment.R
import com.cheng.youthapartment.util.showToast
import com.google.android.material.snackbar.Snackbar

/**
 *
 * @author Cheng
 * @since 2025/1/4
 */
class UserCenterFragment : Fragment() {
    private val activity by lazy { requireActivity() as HomeActivity }
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
    }

    private fun initView() {
        view.findViewById<Button>(R.id.aaaaaa_user).setOnClickListener { btnView ->
            Snackbar.make(view,"是否确认删除？", Snackbar.LENGTH_SHORT)
                .setAction("确认") {
                    "已删除".showToast(activity)
                }
                .show()
        }
    }
}