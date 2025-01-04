package com.cheng.youthapartment.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 *
 * @author Cheng
 * @since 2024/12/30
 */
class FragmentAdapter(
    fragmentActivity: FragmentActivity,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
