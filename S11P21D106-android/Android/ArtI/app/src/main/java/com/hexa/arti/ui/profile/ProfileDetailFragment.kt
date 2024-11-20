package com.hexa.arti.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentProfileDetailBinding
import com.hexa.arti.ui.MainActivity
import com.hexa.arti.ui.MainActivityViewModel
import com.hexa.arti.ui.MyGalleryActivityViewModel
import com.hexa.arti.util.handleImage
import com.hexa.arti.util.isPasswordValid
import com.hexa.arti.util.popBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ProfileDetailFragment : BaseFragment<FragmentProfileDetailBinding>(R.layout.fragment_profile_detail) {

    private val profileDetailViewModel: ProfileDetailViewModel by viewModels()
    private val myGalleryActivityViewModel : MyGalleryActivityViewModel by activityViewModels()
    private val ars : ProfileDetailFragmentArgs by navArgs()
    private lateinit var userToken : String
    override fun init() {
        initView()
        initEvent()
    }

    private fun initView(){


        profileDetailViewModel.successStatus.observe(viewLifecycleOwner){
            when(it){
                1 ->{
                    makeToast("프로필이 수정되었습니다")
                    myGalleryActivityViewModel.updateNickname(binding.profileDetailNickEt.text.toString())
                    popBackStack()
                    profileDetailViewModel.updateSuccessStatus()
                }
                2->{
                    makeToast("오류가 발생했습니다. 다시 시도해주세요")
                }
                3->{
                    makeToast("비밀번호가 변경되었습니다.")
                    popBackStack()
                    profileDetailViewModel.updateSuccessStatus()
                }
                else ->{

                }
            }

        }
        myGalleryActivityViewModel.nickname.observe(viewLifecycleOwner){
            binding.profileDetailNickEt.setText(it)
        }
        with(binding){

            when(ars.detailType){
                0-> {
                    profileDetailModifyCl.visibility = View.VISIBLE
                }
                1->{
                    profilePassModifyCl.visibility = View.VISIBLE
                }
                2->{
                    profileInfoCl.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun initEvent(){
        with(binding){
            profilePassBackBtn.setOnClickListener { popBackStack() }
            profileDetailCancelBtn.setOnClickListener { popBackStack() }
            profilePassCancelBtn.setOnClickListener { popBackStack() }
            profileInfoBackBtn.setOnClickListener { popBackStack() }
            // 프로필 수정
            profileDetailModifyBtn.setOnClickListener {
                profileDetailViewModel.changeNickname(profileDetailNickEt.text.toString())

//                popBackStack()
            }
            // 비밀번호 수정
            profilePassModifyBtn.setOnClickListener {
                val passOld = profilePassOldEt.text.toString()
                val passNew =  profilePassNewEt.text.toString()
                val passNewCheck = profilePassNewCheckEt.text.toString()
                if(passOld.isEmpty() || passNew.isEmpty() || passNewCheck.isEmpty()){
                    makeToast("빈 값이 있습니다. 입력해주세요")
                }
                else if(!isPasswordValid(passNew)){
                    makeToast("비밀번호는 영대소문자 + 특수문자 조합으로 9자리 이상입니다.")
                }
                else if(passNew != passNewCheck){
                    makeToast("새 비밀번호가 같은지 확인해주세요")
                }
                else{
                    profileDetailViewModel.changePass(profilePassOldEt.text.toString(), profilePassNewEt.text.toString(), profilePassNewCheckEt.text.toString())
//                    popBackStack()
                }
            }

        }
    }

}