package com.cheng.youthapartment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import com.cheng.youthapartment.databinding.ItemActionSheetOptionBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * 分段选择器
 * @author CHENG
 * @since 2025/3/26
 */

class ActionSheetAdapter(
    private val context: Context,
    private val options: List<String>,
    initialSelectedPosition: Int,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ActionSheetAdapter.ViewHolder>() {

    var selectedPosition: Int = initialSelectedPosition
        private set

    // 在构造函数或 init 块中获取主题颜色
    @ColorInt
    private val primaryColor: Int = run {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary,
            typedValue,
            true
        )
        typedValue.data
    }

    // ViewHolder 使用 ViewBinding
    inner class ViewHolder(val binding: ItemActionSheetOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            // 设置点击监听器
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previousSelectedPosition = selectedPosition
                    selectedPosition = position

                    // 更新UI：刷新之前选中和当前选中的项
                    if (previousSelectedPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(previousSelectedPosition)
                    }
                    notifyItemChanged(selectedPosition)

                    // 执行回调
                    onItemClick(selectedPosition)
                }
            }
        }

        fun bind(option: String, isSelected: Boolean) {
            binding.tvOption.text = option
            if (isSelected) {
                binding.tvOption.setTextColor(Color.BLACK)
                binding.tvOption.setTypeface(null, Typeface.BOLD)
            } else {
                binding.tvOption.setTextColor("#C2C2C2".toColorInt())
                binding.tvOption.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActionSheetOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(options[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = options.size

    // 获取当前选中的选项文本
    fun getSelectedItem(): String? {
        return if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < options.size) {
            options[selectedPosition]
        } else {
            null
        }
    }
}