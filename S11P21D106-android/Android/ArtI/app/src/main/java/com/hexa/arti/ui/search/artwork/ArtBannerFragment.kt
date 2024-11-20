package com.hexa.arti.ui.search.artwork

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentArtBannerBinding
import com.hexa.arti.ui.MainActivityViewModel
import com.hexa.arti.ui.search.adapter.ArtworkAdapter
import com.hexa.arti.util.LoadingRecommendDialog
import com.hexa.arti.util.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtBannerFragment : BaseFragment<FragmentArtBannerBinding>(R.layout.fragment_art_banner) {

    private var isShowDialog = false
    private lateinit var loadingDialog: LoadingRecommendDialog

    private val viewModel: ArtBannerViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private val artAdapter = ArtworkAdapter { artwork ->
        val action =
            ArtBannerFragmentDirections.actionArtBannerFragmentToArtDetailFragment(
                imgId = artwork.artworkId,
                imgTitle = artwork.title,
                imgUrl = artwork.imageUrl,
                imgYear = artwork.year,
                imgArtist = artwork.artist.toString(),
                galleryId = -1,
            )
        navigate(action)
    }


    override fun init() {
        binding.rvArt.adapter = artAdapter

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        showLoadingDialog()
        initObserve()
        initUserData()
    }

    private fun initUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if(viewModel.resultArtworks.value == null) {
                    mainActivityViewModel.getLoginData().collect { userData ->
                        userData?.let {
                            viewModel.getRecommendArtworks(userData.memberId)
                        }
                    }
                }
            }
        }
    }

    private fun initObserve() {
        viewModel.resultArtworks.observe(viewLifecycleOwner) {
            artAdapter.submitList(it)
            hideLoadingDialog()
        }
    }

    fun showLoadingDialog() {
        if (!isShowDialog) {
            isShowDialog = true
            loadingDialog = LoadingRecommendDialog()
            loadingDialog.isCancelable = false
            loadingDialog.show(mainActivity.supportFragmentManager, "loading")
        }
    }

    fun hideLoadingDialog() {
        if (isShowDialog) {
            isShowDialog = false
            loadingDialog.dismiss()
        }
    }
}