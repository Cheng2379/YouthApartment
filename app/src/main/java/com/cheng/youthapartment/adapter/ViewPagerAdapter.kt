package com.cheng.youthapartment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheng.youthapartment.R
import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.databinding.ItemBannerBinding

/**
 *
 * @author CHENG
 * @since 2025/2/7
 */
class ViewPagerAdapter(
    private val imageList: List<GraphVo>,
    val context: Context
) :
    RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerAdapter.PagerViewHolder {
        return PagerViewHolder(
            ItemBannerBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.PagerViewHolder, position: Int) {
        Glide.with(context)
            .load(imageList[position].url)
            .error(R.drawable.img_fail)
            .centerCrop()
            .into(holder.binding.bannerImage)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class PagerViewHolder(val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root)
}