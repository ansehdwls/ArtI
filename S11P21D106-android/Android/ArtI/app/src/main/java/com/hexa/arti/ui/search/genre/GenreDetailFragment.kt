package com.hexa.arti.ui.search.genre

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hexa.arti.R
import com.hexa.arti.config.ApplicationClass
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.databinding.FragmentGenreDetailBinding
import com.hexa.arti.ui.search.adapter.ArtworkAdapter
import com.hexa.arti.util.navigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenreDetailFragment :
    BaseFragment<FragmentGenreDetailBinding>(R.layout.fragment_genre_detail) {

    private val args: GenreDetailFragmentArgs by navArgs()
    private val viewModel: GenreDetailViewModel by viewModels()

    private val artAdapter = ArtworkAdapter { artwork ->
        val action =
            GenreDetailFragmentDirections.actionGenreDetailFragmentToArtDetailFragment(
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
        Log.d("확인","args 값 ${args.genreName}")
        initViews()
        initObserve()

        if (viewModel.resultArtwork.value == null) {
            Log.d("확인","데이터 호출")
            viewModel.getGenreRandomData(args.genreName)
        }
    }

    private fun initObserve() {
        viewModel.resultArtwork.observe(viewLifecycleOwner) { resultArtworks ->
            artAdapter.submitList(resultArtworks)
        }
    }

    private fun initViews() {

        binding.tvGenreDetail.text = ApplicationClass.ENGLISH_TO_KOREAN_MAP[args.genreName]

        binding.rvArt.adapter = artAdapter

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }
}