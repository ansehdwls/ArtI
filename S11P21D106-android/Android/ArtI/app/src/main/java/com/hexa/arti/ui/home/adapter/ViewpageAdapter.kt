package com.hexa.arti.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hexa.arti.R
import com.hexa.arti.data.model.home.GetRecommendGalleriesResponse
import com.hexa.arti.databinding.ItemHomepageBinding

class ViewpageAdapter(
    private val onPlayClick: (GetRecommendGalleriesResponse) -> Unit,
    private val onSliding: () -> Unit,
    private val onNormal: () -> Unit,
    private val onRefresh: () -> Unit,
) : RecyclerView.Adapter<ViewpageAdapter.ViewHolder>() {

    private val items = mutableListOf<GetRecommendGalleriesResponse>()

    class ViewHolder(
        private val binding: ItemHomepageBinding,
        private val onPlayClick: (GetRecommendGalleriesResponse) -> Unit,
        private val onSliding: () -> Unit,
        private val onNormal: () -> Unit,
        private val onRefresh: () -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val themeAdapter = ThemeAdapter()

        fun bind(item: GetRecommendGalleriesResponse) {
            val bottomSheet = binding.includeBottomSheet.bottomSheetLayout
            val bottomSheetBehavior: BottomSheetBehavior<View> =
                BottomSheetBehavior.from(bottomSheet)

            binding.ivRefresh.setOnClickListener {
                onRefresh()
            }

            bottomSheetBehavior.apply {
                isHideable = false
                isFitToContents = false
                isDraggable = true
                var lastOffset = 0f
                var stateFlag = 0

                addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_DRAGGING -> {
                                onSliding()
                            }

                            BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_HALF_EXPANDED, BottomSheetBehavior.STATE_SETTLING -> {
                                onNormal()
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        if (lastOffset < slideOffset) {
                            if (slideOffset > 0.25f) {
                                stateFlag = 1
                            }
                        } else {
                            if (slideOffset < 0.806) {
                                stateFlag = 2
                            }
                        }
                        lastOffset = slideOffset
                    }
                })

            }


            binding.includeBottomSheet.ivPlay.setOnClickListener {
                onPlayClick(item)
            }

            Glide.with(binding.root.context)
                .load(item.homeGallery.galleryThumbnail)
                .error(R.drawable.temp_night_star)
                .into(binding.ivThumbnail)

            binding.includeBottomSheet.tvArtmuseumTitle.text = item.homeGallery.galleryTitle
            binding.includeBottomSheet.tvIntroduceContent.text =
                if (item.homeGallery.galleryDescription.isNullOrBlank()) "소개가 없습니다." else item.homeGallery.galleryDescription

            binding.includeBottomSheet.rvTheme.apply {
                adapter = themeAdapter
                themeAdapter.submitList(item.homeThemes)
            }
        }

    }

    fun submitList(newItems: List<GetRecommendGalleriesResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHomepageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onPlayClick, onSliding, onNormal, onRefresh)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}