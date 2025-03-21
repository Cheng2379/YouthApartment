package com.cheng.youthapartment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cheng.youthapartment.R
import com.cheng.youthapartment.bean.properties.GraphBean
import com.cheng.youthapartment.databinding.ItemBannerBinding

/**
 *
 * @author CHENG
 * @since 2025/2/7
 */
class BannerAdapter(
    private val imageList: List<GraphBean>,
    val context: Context
) :
    RecyclerView.Adapter<BannerAdapter.PagerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BannerAdapter.PagerViewHolder {
        return PagerViewHolder(
            ItemBannerBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BannerAdapter.PagerViewHolder, position: Int) {
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