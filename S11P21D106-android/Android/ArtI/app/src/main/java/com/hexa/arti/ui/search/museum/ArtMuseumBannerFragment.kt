package com.hexa.arti.ui.search.museum

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.artmuseum.GalleryBanner
import com.hexa.arti.databinding.FragmentArtMuseumBannerBinding
import com.hexa.arti.ui.search.adapter.ArtMuseumAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtMuseumBannerFragment :
    BaseFragment<FragmentArtMuseumBannerBinding>(R.layout.fragment_art_museum_banner) {

    private val viewModel: ArtMuseumBannerViewModel by viewModels()

    private val artMuseumAdapter = ArtMuseumAdapter { gallery ->
        moveToArtMuseumFragment(gallery)
    }

    override fun init() {
        initObserve()
        initViews()
    }

    private fun initObserve() {
        viewModel.resultMuseums.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                artMuseumAdapter.submitList(emptyList())
            } else {
                artMuseumAdapter.submitList(it)
            }
        }

    }

    private fun initViews() {
        binding.rvArtMuseum.adapter = artMuseumAdapter

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (viewModel.resultMuseums.value == null) {
            viewModel.getRandomMuseums()
        }
    }

    private fun moveToArtMuseumFragment(gallery: GalleryBanner) {
        findNavController().navigate(
            ArtMuseumBannerFragmentDirections.actionArtMuseumBannerFragmentToArtMuseumFragment(
                gallery
            )
        )
    }

}