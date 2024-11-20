package com.hexa.arti.ui.artwork

import android.animation.Animator
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.navArgs
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentIsSelectCreateImageBinding
import com.hexa.arti.util.navigate
import com.hexa.arti.util.popBackStack

class IsSelectCreateImageFragment :
    BaseFragment<FragmentIsSelectCreateImageBinding>(R.layout.fragment_is_select_create_image) {

    private val args: IsSelectCreateImageFragmentArgs by navArgs()
    private var animator: Animator? = null

    override fun init() {
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        if (mainActivity.isUp) startProgressBarAnimation(40, 60)
        else {
            if (!mainActivity.isDoubleUp) startProgressBarAnimation(100, 60)
            else startProgressBarAnimation(80, 60)
        }
        mainActivity.isUp = false

        with(binding) {
            artworkCommentTv.startAnimation(fadeInAnimation)
            artworkMaintainBtn.startAnimation(fadeInAnimation)
            artworkCreateBtn.startAnimation(fadeInAnimation)
            //뒤로가기
            artworkBackBtn.setOnClickListener {
                popBackStack()
            }

            //네
            artworkCreateBtn.setOnClickListener {
                mainActivity.isUp = true
                val action =
                    IsSelectCreateImageFragmentDirections.actionIsSelectCreateImageFragmentToImageUploadFragment(
                        args.artId
                    )
                navigate(action)
            }
            // 아니오
            artworkMaintainBtn.setOnClickListener {
                Log.d("확인", "init: ${args.artId.toString()}")
                mainActivity.isDoubleUp = true
                val action =
                    IsSelectCreateImageFragmentDirections.actionIsSelectCreateImageFragmentToArtworkResultFragment(
                        args.artId.toString(),
                        0
                    )
                navigate(action)
            }
        }

    }

    private fun startProgressBarAnimation(start: Int, end: Int) {
        animator = ValueAnimator.ofInt(start, end).apply {
            duration = 800
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                binding.progressBar.progress = progress
            }
        }
        animator?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animator?.cancel()
    }

}