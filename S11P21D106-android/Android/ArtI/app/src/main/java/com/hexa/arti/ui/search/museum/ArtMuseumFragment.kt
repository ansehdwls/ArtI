package com.hexa.arti.ui.search.museum

import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hexa.arti.R
import com.hexa.arti.config.BaseFragment
import com.hexa.arti.data.model.artmuseum.GalleryBanner
import com.hexa.arti.data.model.artmuseum.ThemeArtwork
import com.hexa.arti.databinding.FragmentArtMuseumBinding
import com.hexa.arti.ui.MainActivityViewModel
import com.hexa.arti.ui.home.adapter.ThemeAdapter
import com.hexa.arti.ui.search.adapter.PreviewAdapter
import com.hexa.arti.util.asHomeTheme
import com.hexa.arti.util.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtMuseumFragment : BaseFragment<FragmentArtMuseumBinding>(R.layout.fragment_art_museum) {

    private val viewModel: ArtMuseumViewModel by viewModels()
    private val args: ArtMuseumFragmentArgs by navArgs()
    private val previewAdapter = PreviewAdapter { clickedImage ->
        changeFocusItem(clickedImage)
    }
    private val themeAdapter = ThemeAdapter()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private var isBookmarked = false
    private var memberId = 0

    override fun init() {
        Log.d("확인", "갤러리 ${args.gallery}")
        initObserve()
        initViews(args.gallery)
        initUserData()
    }

    private fun initObserve() {
        viewModel.resultTotalTheme.observe(viewLifecycleOwner) { resultTotalTheme ->
            if (resultTotalTheme.isEmpty()) {
                Log.d("확인", "통신에러")
            } else {
                val totalArtworks = mutableListOf<ThemeArtwork>()
                for (theme in resultTotalTheme) {
                    for (artwork in theme.artworks) {
                        totalArtworks.add(artwork)
                    }
                }
                if (totalArtworks.isEmpty()) {
                    binding.clTotalMuseum.visibility = View.GONE
                    binding.tvNoArtworks.visibility = View.VISIBLE
                } else {
                    totalArtworks[0] = totalArtworks[0].copy(isFocus = true)
                    Glide.with(requireContext())
                        .load(totalArtworks[0].imageUrl)
                        .error(R.drawable.gallery_sample2)
                        .into(binding.ivArtImage)

                    binding.tvArtTitle.text = totalArtworks[0].title
                    previewAdapter.submitList(totalArtworks)

                    themeAdapter.submitList(resultTotalTheme.map { it.asHomeTheme() })
                }
            }
        }

        viewModel.subscriptionGallery.observe(viewLifecycleOwner) {
            Log.d("확인", "미술관 id ${args.gallery.galleryId} ${it}")
            if (it.any { it.galleryId == args.gallery.galleryId }) {
                binding.ivBookmark.setImageResource(R.drawable.ic_bookmark)
                isBookmarked = true
            }
        }

        viewModel.subscribeResult.observe(viewLifecycleOwner) {
            makeToast(it)
        }
    }

    private fun initViews(gallery: GalleryBanner) {
        binding.rvPreview.adapter = previewAdapter

        binding.rvTheme.adapter = themeAdapter

        viewModel.getTotalThemes(gallery.galleryId)

        binding.ivRight.setOnClickListener {
            val nextFocusedIndex = previewAdapter.currentList.indexOfFirst { it.isFocus } + 1

            if (nextFocusedIndex >= previewAdapter.currentList.size) return@setOnClickListener

            changeFocusItem(previewAdapter.currentList[nextFocusedIndex])

            binding.rvPreview.layoutManager?.smoothScrollToPosition(
                binding.rvPreview,
                RecyclerView.State(),
                nextFocusedIndex
            )
        }

        binding.ivLeft.setOnClickListener {

            val prevFocusedIndex = previewAdapter.currentList.indexOfFirst { it.isFocus } - 1

            if (prevFocusedIndex < 0) return@setOnClickListener

            changeFocusItem(previewAdapter.currentList[prevFocusedIndex])

            binding.rvPreview.layoutManager?.smoothScrollToPosition(
                binding.rvPreview,
                RecyclerView.State(),
                prevFocusedIndex
            )
        }

        binding.ivBookmark.setOnClickListener {
            if (isBookmarked) {
                binding.ivBookmark.setImageResource(R.drawable.ic_unbookmark)
                viewModel.unSubscribe(memberId, args.gallery.galleryId)
            } else {
                binding.ivBookmark.setImageResource(R.drawable.ic_bookmark)
                viewModel.subscribe(memberId, args.gallery.galleryId)
            }
            isBookmarked = !isBookmarked

        }

        binding.tvMuseumTitle.text = gallery.name
        binding.tvIntroduceContent.text = gallery.description

        binding.ivPlay.setOnClickListener {
            navigate(
                ArtMuseumFragmentDirections.actionArtMuseumFragmentToArtGalleryDetailFragment(
                    galleryId = args.gallery.galleryId, galleryName = binding.tvMuseumTitle.text.toString()
                )
            )
        }

    }

    private fun changeFocusItem(clickedImage: ThemeArtwork) {
        val updateList = previewAdapter.currentList.map { previewImage ->
            previewImage.copy(isFocus = (previewImage.id == clickedImage.id))
        }

        binding.tvArtTitle.text = clickedImage.title

        Glide.with(requireContext())
            .load(clickedImage.imageUrl)
            .error(R.drawable.gallery_sample2)
            .into(binding.ivArtImage)

        previewAdapter.submitList(updateList)
    }

    private fun initUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.getLoginData().collect { userData ->
                    CoroutineScope(Dispatchers.Main).launch {
                        userData?.let {
                            memberId = userData.memberId
                            viewModel.getSubscriptionGalleries(userData.memberId)
                        }
                    }
                }
            }
        }
    }


}