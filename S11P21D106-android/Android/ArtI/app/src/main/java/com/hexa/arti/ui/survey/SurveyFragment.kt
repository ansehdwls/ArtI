package com.hexa.arti.ui.survey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.survey.SurveyResponseItem
import com.hexa.arti.databinding.FragmentSurveyBinding
import com.hexa.arti.ui.LoginActivity
import com.hexa.arti.ui.MainActivity
import com.hexa.arti.util.navigate
import com.hexa.arti.util.popBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SurveyFragment : BaseFragment<FragmentSurveyBinding>(R.layout.fragment_survey){

    private val surveyViewModel : SurveyViewModel by viewModels()
    private lateinit var  surveyImages : Array<Array<SurveyResponseItem>>
    private var currentPoint = 0
    private var status = 0

    private lateinit var imageViews: List<ImageView>
    private var clickedArtworkIds = mutableListOf(0, 0, 0, 0, 0)

    override fun init() {
        surveyViewModel.getSurveyImage()

        imageViews = listOf(
            binding.imageView1,
            binding.imageView2,
            binding.imageView3,
            binding.imageView4,
            binding.imageView5,
            binding.imageView6,
            binding.imageView7,
            binding.imageView8,
            binding.imageView9
        )

        mainActivity.showLoadingDialog()
        surveyViewModel.surveyImage.observe(viewLifecycleOwner){
            mainActivity.hideLoadingDialog()

            surveyImages = it

            initEvent()

        }


        binding.surveyGridLayout.setOnClickListener{
            if (!click) return@setOnClickListener


        }

        // 뒤로가기 이벤트
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            status -= 20
            if(status < 0) {
                mainActivity.startActivity(Intent(requireContext(),LoginActivity::class.java))
                mainActivity.finish()
            }
            else{
                binding.progressBar.progress = status
                binding.surveyProgressTv.text = "$status%"
            }

            if(currentPoint > 0 ){
                currentPoint--
                initEvent()
            }
        }

    }

    fun initEvent(){
        surveyImages[currentPoint].forEachIndexed { index, surveyItem ->
            if (index < imageViews.size) {
                // Glide 등을 사용하여 URL로부터 이미지 로드
                Glide.with(requireContext())
                    .load(surveyItem.filename)
                    .into(imageViews[index])

                // ImageView에 클릭 리스너 설정
                imageViews[index].setOnClickListener {
                    if (!click) return@setOnClickListener
                    currentPoint++;
                    // 클릭한 항목의 artwork_id 출력
                        val clickedArtworkId = surveyItem.artwork_id
                        // 이 부분에 ID를 사용하는 로직 추가 (예: 로그 출력)
                        Log.d("SurveyFragment", "Clicked artwork ID: $clickedArtworkId")
                    addArtworkId(clickedArtworkId)

                    Log.d("확인", "initEvent: ${clickedArtworkIds}")
                    click = false
                    status += 20

                    CoroutineScope(Dispatchers.Main).launch {
                        for (i in status-20..status) {
                            binding.progressBar.progress = i
                            delay(20L) // 속도를 조절하려면 이 값을 변경하세요.

                            binding.surveyProgressTv.text = "$i%"
                        }
                        click = true
                        if(status >= 99) {
                            val action = SurveyFragmentDirections.actionSurveyFragmentToCreateGalleryFragment(clickedArtworkIds.toIntArray())
                            navigate(action)
                        }
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        fadeOutAndIn(binding.surveyGridLayout)
                    }
                    if(currentPoint < 5) initEvent()
                }
            }
        }
    }
    private var click = true
    private fun addArtworkId(artworkId: Int) {
        // 리스트의 첫 번째 값을 제거하고, 새로운 artwork_id를 추가하여 5개의 아이템을 유지
        clickedArtworkIds.removeAt(0)  // 리스트의 첫 번째 값 제거
        clickedArtworkIds.add(artworkId)  // 새로 클릭된 artwork_id 추가

        // 리스트의 현재 상태를 로그로 출력 (디버깅용)
        Log.d("SurveyFragment", "Current clicked IDs: $clickedArtworkIds")
    }
    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(true)
    }

    // fade_in, fade_out 애니메이션
    private fun fadeOutAndIn(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(250)
            .withEndAction {
                view.animate()
                    .alpha(1f)
                    .setDuration(250)
                    .start()
            }
            .start()
    }

}