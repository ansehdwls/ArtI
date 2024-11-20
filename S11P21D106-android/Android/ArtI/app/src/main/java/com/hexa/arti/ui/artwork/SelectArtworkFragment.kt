package com.hexa.arti.ui.artwork

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.View.GONE
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentSelectArtworkBinding
import com.hexa.arti.ui.artwork.adapter.SelectArtworkAdapter
import com.hexa.arti.util.navigate
import com.hexa.arti.util.popBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "SelectArtworkFragment"

@AndroidEntryPoint
class SelectArtworkFragment :
    BaseFragment<FragmentSelectArtworkBinding>(R.layout.fragment_select_artwork) {

    private val selectArtworkViewModel: SelectArtworkViewModel by viewModels()
    private lateinit var adapter: SelectArtworkAdapter
    private var isClicked = false
    private var animator: Animator? = null

    override fun init() {

        if (mainActivity.isUp) startProgressBarAnimation(20, 40)
        else startProgressBarAnimation(60, 40)
        mainActivity.isUp = false

        val fadeInAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.artwork_fade_in_slide_up)

        binding.artworkCommentTv.startAnimation(fadeInAnimation)
        binding.artworkSearchTl.startAnimation(fadeInAnimation)

        adapter = SelectArtworkAdapter(onClick = { id ->
            mainActivity.isUp = true
            val action =
                SelectArtworkFragmentDirections.actionSelectArtworkFragmentToIsSelectCreateImageFragment(
                    id
                )
            navigate(action)
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectArtworkViewModel.artWorkResult.collect { pagingData ->
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.noSearchTv.visibility = GONE
                        mainActivity.hideLoadingDialog()
                        adapter.submitData(pagingData)
                    }
                }
            }
        }
//        selectArtworkViewModel.artWorkResult.observe(viewLifecycleOwner) {
//            isClicked = false
//            Log.d(TAG, "init: ${it}")
//            if (it.isNotEmpty()) {
//                binding.noSearchTv.visibility = GONE
//                Log.d(TAG, "init: bb")
//                mainActivity.hideLoadingDialog()
//                tempList = it.toList()
//                adapter.submitList(tempList)
//
//            }
//            else{
//                mainActivity.hideLoadingDialog()
//                binding.noSearchTv.visibility = VISIBLE
//            }
//        }

        with(binding) {

            selectArtworkRv.adapter = adapter
            if (!isClicked) {

                artworkSearchEt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
                    ) {
                        // 검색 또는 엔터 키가 눌렸을 때
                        val imm =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.windowToken, 0)

                        isClicked = true
                        selectArtworkViewModel.getArtworkByString(artworkSearchEt.text.toString())
                        mainActivity.showLoadingDialog()
                        return@OnEditorActionListener true
                    }
                    false
                })
            }

            artworkBackBtn.setOnClickListener { popBackStack() }
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