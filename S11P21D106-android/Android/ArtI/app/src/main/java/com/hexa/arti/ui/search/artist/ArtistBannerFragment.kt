package com.hexa.arti.ui.search.artist

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.search.Artist
import com.hexa.arti.databinding.FragmentArtistBannerBinding
import com.hexa.arti.ui.search.adapter.ArtistAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistBannerFragment :
    BaseFragment<FragmentArtistBannerBinding>(R.layout.fragment_artist_banner) {

    private val viewModel: ArtistBannerViewModel by viewModels()

    private val artistAdapter = ArtistAdapter { artist ->
        moveToArtistDetailFragment(artist)
    }

    override fun init() {
        initObserve()
        initViews()

        if (viewModel.resultArtists.value.isNullOrEmpty()) {
            viewModel.getRandomArtists()
        }
    }

    private fun initObserve() {
        viewModel.resultArtists.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                artistAdapter.submitList(emptyList())
            } else {
                artistAdapter.submitList(it)
            }
        }

    }

    private fun initViews() {
        binding.rvArtist.adapter = artistAdapter

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun moveToArtistDetailFragment(artist: Artist) {
        findNavController().navigate(
            ArtistBannerFragmentDirections.actionArtistBannerFragmentToArtistDetailFragment(
                artist
            )
        )
    }
}