package com.hexa.arti.ui.survey

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentInstargramSurveyBinding
import com.hexa.arti.util.navigate

class InstagramSurveyFragment : BaseFragment<FragmentInstargramSurveyBinding>(R.layout.fragment_instargram_survey) {

    private val instagramSurveyViewModel : InstagramSurveyViewModel by viewModels()
    override fun init() {

        if(instagramSurveyViewModel.status == 2){
            binding.instagramCancelBtn.text = "확인"
            binding.instagramBtn.visibility = View.GONE
        }


        with(binding){
            instagramBtn.setOnClickListener {
                instagramSurveyViewModel.updateStatus()
                navigate(R.id.action_instagramSurveyFragment_to_instagramFragment)
            }
            instagramCancelBtn.setOnClickListener {
                navigate(R.id.action_instagramSurveyFragment_to_homeFragment)
            }
        }
    }

}