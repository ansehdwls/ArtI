package com.hexa.arti.ui.search.genre

import androidx.navigation.fragment.findNavController
import com.hexa.arti.R
import com.hexa.arti.config.ApplicationClass.Companion.REPRESENT_ARTWORKS
import com.hexa.arti.config.ApplicationClass.Companion.REPRESENT_ARTWORKS_KOR
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.search.Genre
import com.hexa.arti.databinding.FragmentGenreBannerBinding
import com.hexa.arti.ui.search.adapter.GenreAdapter


class GenreBannerFragment :
    BaseFragment<FragmentGenreBannerBinding>(R.layout.fragment_genre_banner) {

    private val genreAdapter = GenreAdapter { genreName ->
        goToGenreDetailFragment(genreName)
    }


    override fun init() {
        initViews()
    }

    private fun initViews() {

        binding.rvGenre.adapter = genreAdapter

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val representGenres = mutableListOf<Genre>()

        for (index in 0..<REPRESENT_ARTWORKS.size) {
            representGenres.add(
                Genre(
                    id = index,
                    title = REPRESENT_ARTWORKS[index],
                    titleKor = REPRESENT_ARTWORKS_KOR[index]
                )
            )
        }

        genreAdapter.submitList(representGenres)
    }

    private fun goToGenreDetailFragment(genreName: String) {
        findNavController().navigate(
            GenreBannerFragmentDirections.actionGenreBannerFragmentToGenreDetailFragment(
                genreName
            )
        )
    }
}