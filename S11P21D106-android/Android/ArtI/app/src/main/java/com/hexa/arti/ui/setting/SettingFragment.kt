package com.hexa.arti.ui.setting

import androidx.viewpager2.widget.ViewPager2
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentSettingBinding
import com.hexa.arti.ui.setting.adapter.MyPageAdapter

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {


    override fun init() {

        with(binding) {
            settingViewPager.adapter = MyPageAdapter(requireActivity())
            // ViewPager 페이지 변경 시 콜백 설정
            settingViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    // 현재 선택된 페이지에 따라 스타일을 변경
                    when (position) {
                        0 -> {
                            settingPortfolioTv.setTextAppearance(
                                requireContext(),
                                R.style.SelectTextTitle
                            )
                            settingMyPageTv.setTextAppearance(
                                requireContext(),
                                R.style.UnSelectTextTitle
                            )
                            settingPortfolioV.setBackgroundColor(resources.getColor(R.color.black))
                            settingMyPageV.setBackgroundColor(resources.getColor(R.color.disable_color))
                        }

                        1 -> {
                            settingPortfolioTv.setTextAppearance(
                                requireContext(),
                                R.style.UnSelectTextTitle
                            )
                            settingMyPageTv.setTextAppearance(
                                requireContext(),
                                R.style.SelectTextTitle
                            )
                            settingPortfolioV.setBackgroundColor(resources.getColor(R.color.disable_color))
                            settingMyPageV.setBackgroundColor(resources.getColor(R.color.black))
                        }
                    }
                }
            })


            // 버튼 클릭 시 해당 페이지로 이동
            portfolioBtn.setOnClickListener {
                settingViewPager.currentItem = 0  // 포트폴리오 페이지로 이동

            }

            myPageBtn.setOnClickListener {
                settingViewPager.currentItem = 1  // 내 정보 페이지로 이동

            }

        }
    }

}