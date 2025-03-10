package com.cheng.youthapartment.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.apartment.ApartmentItemVo
import com.cheng.youthapartment.decoration.GridSpaceItemDecoration

/**
 *
 * @author CHENG
 * @since 2025/3/2
 */
class ApartmentItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {
    private val view: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.item_apartment_item, this, false)
    }

    init {
        addView(view)
    }

    @SuppressLint("SetTextI18n")
    fun setData(apartmentItemVo: ApartmentItemVo?) {
        apartmentItemVo?.let { vo ->
            view.findTextById(R.id.room_name).text = vo.name
            view.findTextById(R.id.room_location).text =
                vo.provinceName + " " + vo.cityName + " " + vo.districtName
            view.findTextById(R.id.search_item_room_rent).text = vo.minRent.toString() + "/月起"

            val graphVoList = vo.graphVoList
            graphVoList.takeIf {
                it.isNotEmpty()
            }.let {
                Glide.with(this)
                    .load(graphVoList[0].url)
                    .apply(
                        RequestOptions.bitmapTransform(SquareCrop(20))
                    )
                    .error(R.drawable.img_fail)
                    .into(view.findViewById(R.id.room_img))
            }

            vo.labelInfoList.takeIf {
                it.isNotEmpty()
            }.let {
                val rvLabel: RecyclerView = view.findViewById(R.id.item_apartment_label)
                rvLabel.visibility = VISIBLE
                val labelSpanCount = minOf(6, vo.labelInfoList.size)
                // 只取前三个
                val labelList = ArrayList(vo.labelInfoList.take(3))
                val labelSpacing = resources.getDimensionPixelSize(R.dimen.label_grid_space)
                rvLabel.layoutManager = GridLayoutManager(context, labelSpanCount)
                // 添加网格间距装饰器（处理首尾无间距）
                rvLabel.addItemDecoration(
                    GridSpaceItemDecoration(
                        spanCount = 3,
                        spacing = labelSpacing,
                        includeEdge = false
                    )
                )
                rvLabel.adapter = RvAdapter(context, labelList, R.layout.item_text_label) { holder, position ->
                    val labelInfo = holder.itemView.findTextById(R.id.item_label)
                    labelInfo.text = it!![position].name
                    // 动态计算 TextView 的宽度
                    val textWidth = labelInfo.paint.measureText(it[position].name).toInt()
                    val padding = resources.getDimensionPixelSize(R.dimen.label_grid_space)
                    val totalWidth = textWidth + padding * 2
                    // 设置 TextView 的宽度
                    val layoutParams = labelInfo.layoutParams
                    layoutParams.width = totalWidth
                    labelInfo.layoutParams = layoutParams
                }
            }
        }
    }

    fun View.findTextById(id: Int): TextView {
        return this.findViewById(id)
    }

}