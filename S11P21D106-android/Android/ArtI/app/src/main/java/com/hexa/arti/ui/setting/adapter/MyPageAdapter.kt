package com.hexa.arti.ui.setting.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hexa.arti.ui.profile.MyPageFragment
import com.hexa.arti.ui.profile.PortfolioFragment

class MyPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2  // 페이지 수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PortfolioFragment()
            1 -> MyPageFragment()
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}
