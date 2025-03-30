package com.cheng.youthapartment.view

import android.content.Context
import android.view.LayoutInflater
import com.cheng.youthapartment.adapter.ActionSheetAdapter
import com.cheng.youthapartment.databinding.BottomActionSheetBinding
import com.cheng.youthapartment.util.Logger
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 *
 * @author CHENG
 * @since 2025/3/27
 */
class BottomActionSheet(private val context: Context) {

    private lateinit var binding: BottomActionSheetBinding
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var adapter: ActionSheetAdapter? = null

    private var options: List<String> = emptyList()
    private var title: String? = null
    private var initialSelectedPosition: Int = -1
    private var currentSelectedPosition: Int = -1 // 内部维护状态

    private var actionListener: OnActionListener? = null

    // 回调接口
    interface OnActionListener {
        fun onConfirm(position: Int, item: String)
        fun onCancel()
    }

    // --- 配置方法 ---
    fun setTitle(title: String): BottomActionSheet {
        this.title = title
        return this
    }

    fun setOptions(options: List<String>): BottomActionSheet {
        this.options = options
        // 重置选中状态，因为选项变了
        this.initialSelectedPosition = -1
        this.currentSelectedPosition = -1
        return this
    }

    fun setInitialSelection(position: Int): BottomActionSheet {
        if (position >= 0 && position < options.size) {
            this.initialSelectedPosition = position
            // 初始化内部状态
            this.currentSelectedPosition = position
        } else {
            this.initialSelectedPosition = -1
            this.currentSelectedPosition = -1
        }
        return this
    }

    fun setActionListener(listener: OnActionListener): BottomActionSheet {
        this.actionListener = listener
        return this
    }

    // --- 控制方法 ---
    fun show() {
        if (options.isEmpty()) {
            Logger.w("Options list cannot be empty.")
            return
        }

        // 使用 ViewBinding 加载布局
        binding = BottomActionSheetBinding.inflate(LayoutInflater.from(context))

        // 创建 BottomSheetDialog
        bottomSheetDialog = BottomSheetDialog(context).apply {
            setContentView(binding.root) // 设置内容视图
        }

        // 设置标题
        binding.tvTitle.text = title ?: "" // 如果 title 为 null，则设置为空字符串

        // 初始化 Adapter
        // 注意：adapter 内部的 selectedPosition 会在点击时改变，
        // 但最终确认时依赖的是 BottomActionSheet 的 currentSelectedPosition
        adapter = ActionSheetAdapter(context, options, initialSelectedPosition) { position ->
            // 当列表项被点击时，更新 BottomActionSheet 内部的选中位置
            currentSelectedPosition = position
        }
        binding.rvOptions.adapter = adapter
        // LayoutManager 已在 XML 中设置

        // 设置取消按钮点击事件
        binding.tvCancel.setOnClickListener {
            actionListener?.onCancel()
            dismiss()
        }

        // 设置确认按钮点击事件
        binding.tvConfirm.setOnClickListener {
            if (currentSelectedPosition != -1 && currentSelectedPosition < options.size) {
                actionListener?.onConfirm(currentSelectedPosition, options[currentSelectedPosition])
            } else {
                // 可以选择处理未选择的情况，例如不做任何事或提示
            }
            dismiss()
        }

        // 显示 Dialog
        bottomSheetDialog?.show()
    }

    fun dismiss() {
        bottomSheetDialog?.dismiss()
    }
}