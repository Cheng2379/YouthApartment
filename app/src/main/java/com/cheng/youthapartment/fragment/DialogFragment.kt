package com.cheng.youthapartment.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

/**
 * @author cheng
 * @date 2024/12/3
 * @Description
 */
class DialogFragment(
    private val context: Context,
    private val layoutId: Int,
    private val onView: (view: View, dialog: Dialog) -> Unit
) : DialogFragment() {

    @SuppressLint("UseKtx")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val view = inflater.inflate(layoutId, container, false)
        onView.invoke(view, dialog!!)
        return view
    }

    override fun onStart() {
        initDialog()
        super.onStart()
    }

    private fun initDialog() {
        dialog?.window?.apply {
            // 设置宽度为屏幕宽度的 80%
            val displayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity?.windowManager?.currentWindowMetrics?.let {
                    displayMetrics.widthPixels = it.bounds.width()
                }
            } else {
                activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            }
            val width = (displayMetrics.widthPixels * 0.8).toInt()
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            setLayout(width, height)

            // 居中显示
            attributes?.gravity = Gravity.CENTER
        }
    }

}