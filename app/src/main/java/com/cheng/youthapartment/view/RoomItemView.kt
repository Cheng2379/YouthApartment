package com.cheng.youthapartment.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.apartment.ApartmentDetailBean
import com.cheng.youthapartment.decoration.grid_view.LabelSpaceDecoration
import com.cheng.youthapartment.util.findImageViewById
import com.cheng.youthapartment.util.findTextViewById

/**
 * 房间item
 * @author CHENG
 * @since 2025/3/2
 */
class RoomItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {
    private val view: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.item_room, this, false)
    }
    private val mName by lazy { view.findTextViewById(R.id.room_name) }
    private val mLocation by lazy { view.findTextViewById(R.id.room_location) }
    private val mRent by lazy { view.findTextViewById(R.id.search_item_room_rent) }
    private val mImg by lazy { view.findImageViewById(R.id.room_img) }
    private val mInfoGroup: FrameLayout by lazy { view.findViewById(R.id.item_info_group) }

    init {
        addView(view)
        mImg.layoutParams.width = resources.getDimensionPixelSize(R.dimen.apartment_item_height)
        mImg.layoutParams.height = resources.getDimensionPixelSize(R.dimen.apartment_item_height)
        mInfoGroup.layoutParams.height = resources.getDimensionPixelSize(R.dimen.apartment_item_height) + 5
    }

    fun setImgWidthAndHeight(height: Int? = null) {
        height?.let {
            mImg.layoutParams.width = it
            mImg.layoutParams.height = it
            mInfoGroup.layoutParams.height = it
        }
    }

    @SuppressLint("SetTextI18n")
    fun setData(apartmentDetailBean: ApartmentDetailBean?) {
        apartmentDetailBean?.let { vo ->
            mName.text = vo.name
            mLocation.text = vo.provinceName + " " + vo.cityName + " " + vo.districtName
            mRent.text = "￥ " + vo.minRent.toString() + "/月起"

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
                    .into(mImg)
            }

            vo.labelInfoList.takeIf {
                it.isNotEmpty()
            }.let {
                val rvLabel: RecyclerView = view.findViewById(R.id.item_apartment_label)
                rvLabel.visibility = VISIBLE

                // 网格布局优化参数
                val spanCount = 3
                rvLabel.layoutManager = GridLayoutManager(context, spanCount)
                val labelList = ArrayList(vo.labelInfoList.take(8))
                val labelSpacing = resources.getDimensionPixelSize(R.dimen.label_grid_space) / 3
                // 使用自定义间距装饰器
                rvLabel.addItemDecoration(
                    LabelSpaceDecoration(
                        spanCount,
                        rightSpacing = labelSpacing,
                        topSpacing = labelSpacing,
                        bottomSpacing = labelSpacing
                    )
                )

                rvLabel.adapter =
                    RvAdapter(context, labelList, R.layout.item_text_label) { holder, position ->
                        val labelText = holder.itemView.findTextViewById(R.id.item_label)
                        labelText.text = labelList[position].name

                        // 确保标签内容完整显示
                        val layoutParams = labelText.layoutParams
                        layoutParams.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                        labelText.layoutParams = layoutParams
                    }
            }
        }
    }


}