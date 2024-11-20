package com.hexa.arti.ui.home

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentHomeBinding
import com.hexa.arti.ui.MainActivityViewModel
import com.hexa.arti.ui.home.adapter.ViewpageAdapter
import com.hexa.arti.util.LoadingRecommendDialog
import com.hexa.arti.util.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private var isShowDialog = false
    private var loadingDialog: LoadingRecommendDialog? = null

    private val viewModel: HomeViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()


    private val viewpageAdapter = ViewpageAdapter(
        onPlayClick = { item ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToArtGalleryDetailFragment(galleryId = item.homeGallery.galleryId, galleryName= item.homeGallery.galleryTitle)
            navigate(action)
        },
        onSliding = {
            binding.viewpager2.isUserInputEnabled = false
        },
        onNormal = {
            binding.viewpager2.isUserInputEnabled = true
        },
        onRefresh = {
            initUserData()
        }
    )

    override fun init() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitConfirmationDialog()
                }
            })

        initAdapter()
        initObserve()
        initViews()
        Log.d("확인","최초화면 ${mainActivityViewModel.recommendedData}")
        if (mainActivityViewModel.recommendedData.isEmpty()) {
            initUserData()
        } else {
            viewpageAdapter.submitList(mainActivityViewModel.recommendedData)
        }
    }

    private fun initUserData() {
        CoroutineScope(Dispatchers.Main).launch {
            showLoadingDialog()
            mainActivityViewModel.getLoginData().collect { userData ->
                userData?.let {
                    Log.d("확인","유저데이터 ${userData}")
                    viewModel.getRecommendGalleries(userData.memberId)
                }
            }
        }
    }

    private fun initObserve() {
        viewModel.resultGalleries.observe(viewLifecycleOwner) {
            viewpageAdapter.submitList(it)
            mainActivityViewModel.recommendedData = it
            hideLoadingDialog()
        }

    }

    private fun initAdapter() {
        binding.viewpager2.adapter = viewpageAdapter

    }

    override fun onPause() {
        super.onPause()
        loadingDialog?.dismiss()
    }

    private fun initViews() {

    }


    fun showLoadingDialog() {
        if (!isShowDialog) {
            isShowDialog = true
            loadingDialog = LoadingRecommendDialog()
            loadingDialog!!.isCancelable = false
            loadingDialog!!.show(mainActivity.supportFragmentManager, "loading")
        }
    }

    fun hideLoadingDialog() {
        if (isShowDialog) {
            isShowDialog = false
            loadingDialog?.dismiss()
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("정말 종료하시겠습니까?")
            .setCancelable(false)
            .setPositiveButton("예") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("아니오", null)
            .show()
    }
}