package com.hexa.arti.ui.search.artist

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.search.Artist
import com.hexa.arti.databinding.FragmentArtistDetailBinding


class ArtistDetailFragment :
    BaseFragment<FragmentArtistDetailBinding>(R.layout.fragment_artist_detail) {

    private val args: ArtistDetailFragmentArgs by navArgs()

    override fun init() {

        val artist = args.artist

        initVies(artist)
    }

    private fun initVies(artist: Artist) = with(binding) {
        ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        Glide.with(requireContext())
            .load(artist.imageUrl)
            .error(R.drawable.basic_artist_profile)
            .apply(RequestOptions.circleCropTransform())
            .into(ivArtistIamge)

        tvArtistName.text = artist.engName
        tvArtistDescription.text = artist.description


    }


}