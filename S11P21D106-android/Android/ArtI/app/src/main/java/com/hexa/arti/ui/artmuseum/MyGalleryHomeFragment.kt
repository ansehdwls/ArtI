package com.hexa.arti.ui.artmuseum

import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentMyGalleryHomeBinding
import com.hexa.arti.ui.artmuseum.adpater.MyGalleryAdapter
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MyGalleryHomeFragment"

@AndroidEntryPoint
class MyGalleryHomeFragment :
    BaseFragment<FragmentMyGalleryHomeBinding>(R.layout.fragment_my_gallery_home) {

    private val args: MyGalleryHomeFragmentArgs by navArgs()
    override fun init() {
        with(binding) {

            myGalleryViewPager.isSaveEnabled = false
            myGalleryViewPager.adapter = MyGalleryAdapter(requireActivity())

            // 현재 항목 설정
            val initialItem = when (args.myGalleryType) {
                1 -> 1
                else -> 0
            }
            myGalleryViewPager.currentItem = initialItem

            // 페이지 변경 콜백 등록
            myGalleryViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if (isAdded) { // 프래그먼트가 액티비티에 연결되어 있는지 체크
                        when (position) {
                            0 -> {
                                subscribeTv.setTextAppearance(
                                    requireContext(),
                                    R.style.SelectTextTitle
                                )
                                myGalleryTv.setTextAppearance(
                                    requireContext(),
                                    R.style.UnSelectTextTitle
                                )
                                subscribeV.setBackgroundColor(resources.getColor(R.color.black))
                                myGalleryV.setBackgroundColor(resources.getColor(R.color.disable_color))
                            }

                            1 -> {
                                subscribeTv.setTextAppearance(
                                    requireContext(),
                                    R.style.UnSelectTextTitle
                                )
                                myGalleryTv.setTextAppearance(
                                    requireContext(),
                                    R.style.SelectTextTitle
                                )
                                subscribeV.setBackgroundColor(resources.getColor(R.color.disable_color))
                                myGalleryV.setBackgroundColor(resources.getColor(R.color.black))
                            }
                        }
                    }
                }
            })

            // 버튼 클릭 리스너 설정
            subscribeBtn.setOnClickListener {
                myGalleryViewPager.currentItem = 0
            }

            myGalleryBtn.setOnClickListener {
                myGalleryViewPager.currentItem = 1
            }
        }
    }
}
