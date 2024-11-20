package com.hexa.arti.ui.artmuseum.adpater

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hexa.arti.ui.artmuseum.MyGalleryFragment
import com.hexa.arti.ui.artmuseum.SubscribeFragment

class MyGalleryAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2  // 페이지 수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubscribeFragment()
            1 -> MyGalleryFragment()
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}