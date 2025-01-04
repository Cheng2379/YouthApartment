package com.cheng.youthapartment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cheng.youthapartment.R

/**
 *
 * @author Cheng
 * @since 2024/12/18
 */
class RvAdapter<T>(
    private var context: Context,
    private var dataList: ArrayList<T>,
    private var layoutId: Int,
    private var callBack: (holder: ViewHolder, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isAllDataLoaded = false

    companion object {
        private const val ITEM_TYPE_NORMAL = 0
        private const val ITEM_TYPE_FOOTER = 1
    }

    fun setAllDataLoaded(isAllDataLoaded: Boolean) {
        this.isAllDataLoaded = isAllDataLoaded
        notifyItemChanged(dataList.size)
    }

    fun getAllDataLoaded(): Boolean {
        return isAllDataLoaded
    }

    fun updateData(position: Int, newItem: T) {
        dataList[position] = newItem
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDta(newList: List<T>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(newItem: T) {
        dataList.add(newItem)
        notifyItemRangeInserted(dataList.size, 1)
    }

    fun addData(newList: List<T>) {
        val startSize = dataList.size
        dataList.addAll(newList)
        notifyItemRangeInserted(startSize, newList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_NORMAL) {
            ViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false))
        } else {
            FooterViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_footer, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return dataList.size + if (isAllDataLoaded) 1 else 0
    }

    fun getDataSize(): Int {
        return dataList.size + if (isAllDataLoaded) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAllDataLoaded && position == dataList.size) {
            ITEM_TYPE_FOOTER
        } else {
            ITEM_TYPE_NORMAL
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            callBack(holder, position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}