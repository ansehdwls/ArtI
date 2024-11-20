package com.hexa.arti.ui.artwork

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentArtworkUploadBinding
import com.hexa.arti.util.navigate
import com.hexa.arti.util.popBackStack
import kotlinx.coroutines.launch

class ArtworkUploadFragment :
    BaseFragment<FragmentArtworkUploadBinding>(R.layout.fragment_artwork_upload) {
    private var animator: Animator? = null

    override fun init() {
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.artwork_fade_in)
        if (mainActivity.isUp) {
            startProgressBarAnimation(0, 20)
        } else {
            startProgressBarAnimation(40, 20)
        }
        with(binding) {
            artworkBackBtn.setOnClickListener {
                mainActivity.isUp = true
                popBackStack()
            }
            artworkNextBtn.setOnClickListener {
                mainActivity.isUp = true
                navigate(R.id.action_artworkUploadFragment_to_selectArtworkFragment)
            }

            artworkCommentTv.startAnimation(fadeInAnimation)
            artworkNextBtn.startAnimation(fadeInAnimation)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainActivity.isUp = true
                    popBackStack()
                }
            })
    }

    private fun startProgressBarAnimation(start: Int, end: Int) {
        lifecycleScope.launch {
            animator = ValueAnimator.ofInt(start, end).apply {
                duration = 800
                addUpdateListener { animation ->
                    val progress = animation.animatedValue as Int
                    binding.progressBar.progress = progress
                }
            }
            animator?.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animator?.cancel()
    }

}