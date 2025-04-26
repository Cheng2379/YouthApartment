package com.cheng.youthapartment.view

import android.animation.ValueAnimator
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.PopupWindow
import androidx.core.graphics.toColorInt

/**
 * 下拉筛选视图
 * @author CHENG
 * @since 2025/4/20
 */
object DropDownFilterViewUtil {

    /**
     * 创建一个带有遮罩层和动画效果的 PopupWindow
     *
     * @param activity 目标视图所在的活动页
     * @param anchorView 具体需要添加窗口视图的目标视图
     * @param layoutResId 视图xml
     * @param animationStyleResId 动画文件. 可不传
     * @param setUpView 下拉视图回调, 回调值为具体的目标视图与弹窗控件
     * @return 返回创建的下拉视图
     */
    fun createDropDownPopupWindow(
        activity: Activity,
        anchorView: View,
        layoutResId: Int,
        animationStyleResId: Int? = null,
        onDismiss: (() -> Unit)? = null,
        setUpView: (popupView: View, popupWindow: PopupWindow) -> Unit
    ): PopupWindow {
        var popupWindow = PopupWindow()

        // 获取菜单栏的位置与高度
        val filterBarLocation = IntArray(2)
        (anchorView.parent as View).getLocationOnScreen(filterBarLocation)
        val filterBarHeight = (anchorView.parent as View).height
        // 创建遮蔽视图, 高度为全屏高度减去筛选栏视图的高度
        val maskView = View(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor("#80000000".toColorInt())
            // 此处设置透明度为0, 下面用动画将其透明度变为1, 从而达成一个较为平滑的遮蔽效果展示
            alpha = 0f
            y = (filterBarLocation[1] + filterBarHeight).toFloat()
            setOnClickListener {
                popupWindow.dismiss()
            }
        }
        val rootViewGroup = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootViewGroup.addView(maskView)
        maskView.animate().alpha(1f).setDuration(300).start()

        val popupView =
            LayoutInflater.from(activity).inflate(layoutResId, null)
        // 强制popupView进行测量, 允许根据自身内容和布局参数来确定自己的尺寸, 并提前获取测量的高度, 再将高度设置为0, 后面再通过动画将其设置为实际显示的高度
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWindowHeight = popupView.measuredHeight
        popupView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)

        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            // 自定义动画
            animationStyleResId?.let { animationStyle = it }
            // 监听窗口关闭删除遮蔽视图
            setOnDismissListener {
                maskView.animate().alpha(0f).setDuration(300)
                    .withEndAction {
                        rootViewGroup.removeView(maskView)
                        onDismiss?.invoke()
                    }
                    .start()
            }
        }
        setUpView(popupView, popupWindow)

        popupWindow.showAsDropDown(anchorView.parent as View)

        // 在主线程内显示动画
        popupView.post {
            val valueAnimator = ValueAnimator.ofInt(0, popupWindowHeight)
            // 插帧
            valueAnimator.interpolator = DecelerateInterpolator()
            // 添加动画更新监听器，在动画的每一帧被触发
            valueAnimator.addUpdateListener { animator ->
                // 获取当前帧的动画值，并赋值给popupView布局参数的高度
                val value = animator.animatedValue as Int
                popupView.layoutParams.height = value
                // 通知系统重新布局popupView，触发视图的测量和绘制过程，使高度的变化立即生效
                popupView.requestLayout()
            }
            valueAnimator.start()
        }

        return popupWindow
    }
}