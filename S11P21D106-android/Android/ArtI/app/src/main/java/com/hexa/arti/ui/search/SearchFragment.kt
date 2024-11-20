package com.hexa.arti.ui.search

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.search.Artist
import com.hexa.arti.databinding.FragmentSearchBinding
import com.hexa.arti.ui.search.adapter.ArtistAdapter
import com.hexa.arti.ui.search.adapter.GalleryAdapter
import com.hexa.arti.ui.search.paging.ArtworkPagingAdapter
import com.hexa.arti.util.asGalleryBanner
import com.hexa.arti.util.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()


    private val galleryAdapter = GalleryAdapter { gallery ->
        val action =
            SearchFragmentDirections.actionSearchFragmentToArtMuseumFragment(gallery.asGalleryBanner())
        navigate(action)
    }

    private val artworkPagingAdapter = ArtworkPagingAdapter { artwork ->
        val action =
            SearchFragmentDirections.actionSearchFragmentToArtDetailFragment(
                imgId = artwork.artworkId,
                imgTitle = artwork.title,
                imgUrl = artwork.imageUrl,
                imgYear = artwork.year,
                imgArtist = artwork.artist.toString(),
                galleryId = -1,
            )
        navigate(action)
    }

    private val artistAdapter = ArtistAdapter { artist ->
        moveToArtistDetailFragment(artist)
    }

    private fun updateConstraintForArtist() {
        val constraintSet = ConstraintSet()

        constraintSet.clone(binding.clSearchResult)

        constraintSet.connect(
            R.id.tv_artist_result,
            ConstraintSet.TOP,
            R.id.rv_art_result,
            ConstraintSet.BOTTOM
        )

        constraintSet.applyTo(binding.clSearchResult)
    }

    override fun init() {


        artworkPagingAdapter.addLoadStateListener { loadStates ->
            if (isAdded) {
                if (loadStates.refresh is LoadState.NotLoading && artworkPagingAdapter.itemCount == 0) {
                    binding.tvNoResultArtwork.visibility = View.VISIBLE
                } else if (loadStates.refresh is LoadState.NotLoading) {
                    binding.tvNoResultArtwork.visibility = View.GONE
                    updateConstraintForArtist()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.state != BASE_STATE) {
                        offSearchFocus()
                        viewModel.state = BASE_STATE
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
        initObserve()
        initAdapters()
        initViews()

        checkState()
    }

    private fun checkState() {
        if (viewModel.state == BASE_STATE) mainActivity.hideBottomNav(false)
        if (viewModel.state == RESULT_STATE) {
            binding.clSearchResult.visibility = View.VISIBLE
            binding.clSearchBanner.visibility = View.GONE
            mainActivity.hideBottomNav(true)
        }
    }

    private fun initObserve() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.artWorkResult.collect { pagingData ->
                    CoroutineScope(Dispatchers.Main).launch {
                        artworkPagingAdapter.submitData(pagingData)
                    }
                }
            }
        }

        viewModel.artistResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.tvNoResultArtist.visibility = View.VISIBLE
                updateConstraintArtist()
            } else {
                binding.tvNoResultArtist.visibility = View.GONE
            }
            artistAdapter.submitList(it)
        }

        viewModel.galleriesResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.tvNoResultArtMuseum.visibility = View.VISIBLE
            } else {
                binding.tvNoResultArtMuseum.visibility = View.GONE
            }
            galleryAdapter.submitList(it)
        }

    }

    private fun updateConstraintArtist() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.clSearchResult)

        constraintSet.connect(
            R.id.tv_artist_result,
            ConstraintSet.TOP,
            R.id.tv_no_result_artwork,
            ConstraintSet.BOTTOM
        )

        constraintSet.applyTo(binding.clSearchResult)
    }

    private fun initAdapters() {
        binding.rvArtMuseumResult.adapter = galleryAdapter
        binding.rvArtResult.adapter = artworkPagingAdapter
        binding.rvArtistResult.adapter = artistAdapter
        val layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        binding.rvArtistResult.layoutManager = layoutManager

    }

    private fun initViews() {
        binding.tietSearch.setOnFocusChangeListener { _, hasFocus ->
            viewLifecycleOwner.lifecycleScope.launch {
                if (hasFocus) {
                    viewModel.state = SEARCH_STATE
                    onSearchFocus()
                }
            }
        }

        binding.tvCancel.setOnClickListener {
            offSearchFocus()
        }

        binding.ivClearText.setOnClickListener {
            binding.tietSearch.setText("")
        }

        binding.tietSearch.addTextChangedListener { text ->
            if (text.toString().isEmpty()) binding.ivClearText.visibility = View.GONE
            else binding.ivClearText.visibility = View.VISIBLE
        }

        binding.tietSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)

                val keyword = v.text.toString()

                viewModel.getSearchGalleries(keyword)
                viewModel.getArtworkByString(keyword)
                viewModel.getArtistByString(keyword)

                viewModel.state = RESULT_STATE

                binding.clSearchResult.visibility = View.VISIBLE
                mainActivity.hideBottomNav(true)

                binding.tilSearch.clearFocus()
                return@OnEditorActionListener true
            }

            false
        })

        binding.ivBannerArt.setOnClickListener {
            moveToArtBannerFragment()
        }

        binding.ivGradationArt.setOnClickListener {
            moveToArtBannerFragment()
        }

        binding.tvArt.setOnClickListener {
            moveToArtBannerFragment()
        }

        binding.ivBannerArtist.setOnClickListener {
            moveToArtistBannerFragment()
        }

        binding.ivGradationArtist.setOnClickListener {
            moveToArtistBannerFragment()
        }

        binding.tvArtist.setOnClickListener {
            moveToArtistBannerFragment()
        }

        binding.ivBannerGenre.setOnClickListener {
            moveToGenreBannerFragment()
        }

        binding.ivGradationGenre.setOnClickListener {
            moveToGenreBannerFragment()
        }

        binding.tvGenre.setOnClickListener {
            moveToGenreBannerFragment()
        }


        binding.ivBannerArtMuseum.setOnClickListener {
            moveToArtMuseumFragment()
        }

        binding.ivGradationArtMuseum.setOnClickListener {
            moveToArtMuseumFragment()
        }

        binding.tvArtMuseum.setOnClickListener {
            moveToArtMuseumFragment()
        }
    }

    private fun onSearchFocus() {

        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.tietSearch, InputMethodManager.SHOW_IMPLICIT)

        binding.tvSearchTitle.visibility = View.GONE
        binding.tvCancel.visibility = View.VISIBLE

        binding.clSearchBanner.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                binding.clSearchBanner.visibility = View.GONE
            }

        binding.clSearchBottom.alpha = 0f
        binding.clSearchBottom.visibility = View.VISIBLE
        binding.clSearchBottom.animate()
            .alpha(1f)
            .setDuration(150)

        binding.clSearchResult.visibility = View.GONE
    }

    private fun offSearchFocus() {

        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        binding.tvSearchTitle.visibility = View.VISIBLE
        binding.tvCancel.visibility = View.GONE

        binding.tietSearch.setText("")
        binding.tietSearch.clearFocus()

        binding.clSearchBottom.visibility = View.GONE
        binding.clSearchResult.visibility = View.GONE

        binding.clSearchBanner.alpha = 0f
        binding.clSearchBanner.visibility = View.VISIBLE
        binding.clSearchBanner.animate()
            .alpha(1f)
            .setDuration(150)

        mainActivity.hideBottomNav(false)
    }

    private fun moveToArtBannerFragment() {
        findNavController().navigate(R.id.action_searchFragment_to_artBannerFragment)
    }

    private fun moveToArtistBannerFragment() {
        findNavController().navigate(R.id.action_searchFragment_to_artistBannerFragment)
    }

    private fun moveToGenreBannerFragment() {
        findNavController().navigate(R.id.action_searchFragment_to_genreBannerFragment)
    }

    private fun moveToArtMuseumFragment() {
        findNavController().navigate(R.id.action_searchFragment_to_artMuseumBannerFragment)
    }

    private fun moveToArtistDetailFragment(artist: Artist) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToArtistDetailFragment(
                artist
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.state == BASE_STATE) mainActivity.hideBottomNav(false)
    }


    companion object {
        const val BASE_STATE = 1
        const val SEARCH_STATE = 2
        const val RESULT_STATE = 3
    }
}