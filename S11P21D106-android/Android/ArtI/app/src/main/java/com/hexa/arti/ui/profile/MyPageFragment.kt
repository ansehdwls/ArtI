package com.hexa.arti.ui.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentMyPageBinding
import com.hexa.arti.ui.MainActivity
import com.hexa.arti.ui.MyGalleryActivityViewModel
import com.hexa.arti.ui.setting.SettingFragmentDirections
import com.hexa.arti.util.navigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page){

    private val myGalleryActivityViewModel : MyGalleryActivityViewModel by activityViewModels()

    override fun init() {
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            myGalleryActivityViewModel.nickname.observe(viewLifecycleOwner){
                myPageProfileNameTv.text = " ${it}님 어서오세요."
            }

            // 프로필 편집
            myPageProfileModifyTv.setOnClickListener {
                val action = SettingFragmentDirections.actionSettingFragmentToProfileDetailFragment(0)
                navigate(action)
            }
            // 비밀번호 수정
            myPagePassTv.setOnClickListener {
                val action = SettingFragmentDirections.actionSettingFragmentToProfileDetailFragment(1)
                navigate(action)
            }
            // 이용약관
            myPageInfoTv.setOnClickListener {
                val action = SettingFragmentDirections.actionSettingFragmentToProfileDetailFragment(2)
                navigate(action)
            }
            //인스타그램
            tvInstagram.setOnClickListener {
                navigate(R.id.action_settingFragment_to_instagramFragment)
            }
            // 로그아웃
            myPageLogoutTv.setOnClickListener { mainActivity.moveLogin() }
        }

    }
}
