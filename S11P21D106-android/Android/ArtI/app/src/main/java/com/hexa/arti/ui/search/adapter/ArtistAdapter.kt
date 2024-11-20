package com.hexa.arti.ui.search.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.hexa.arti.R
import com.hexa.arti.data.model.search.Artist
import com.hexa.arti.databinding.ItemArtistBinding

class ArtistAdapter(
    private val onItemClick: (Artist) -> Unit
) : ListAdapter<Artist, ArtistAdapter.ArtistViewHolder>(ArtistDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ArtistViewHolder(
            ItemArtistBinding.inflate(inflater, parent, false),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArtistViewHolder(
        private val binding: ItemArtistBinding,
        private val onItemClick: (Artist) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(artist: Artist) {

            val circularProgressDrawable = CircularProgressDrawable(binding.root.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

//            val startTime = System.currentTimeMillis()
//
//            Glide.with(binding.root.context)
//                .load(if (artist.imageUrl.isNullOrBlank()) R.drawable.basic_artist_profile else artist.imageUrl)
//                .placeholder(circularProgressDrawable)
////                .override(200, 200)
//                .apply(RequestOptions.circleCropTransform())
//                .into(binding.ivArtist)
//
//            val endTime = System.currentTimeMillis()
//            val executionTime = endTime - startTime
//
//            Log.d("확인","이미지 로딩 속도 확인 $executionTime")

            val startTime = System.currentTimeMillis()

            Glide.with(binding.root.context)
                .load(if (artist.imageUrl.isNullOrBlank()) R.drawable.basic_artist_profile else artist.imageUrl)
                .placeholder(circularProgressDrawable)
                .apply(RequestOptions.circleCropTransform())
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        val endTime = System.currentTimeMillis()
                        val executionTime = endTime - startTime
                        Log.d("확인", "이미지 로딩 실패: $executionTime ms")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        val endTime = System.currentTimeMillis()
                        val executionTime = endTime - startTime
                        Log.d("확인", "이미지 로딩 완료: $executionTime ms")
                        return false
                    }
                })
                .into(binding.ivArtist)


//            Glide.with(binding.ivArtist.context)
//                .load(if (artist.imageUrl.isNullOrBlank()) R.drawable.basic_artist_profile else artist.imageUrl)
//                .error(R.drawable.basic_artist_profile)
//                .apply(RequestOptions.circleCropTransform())
//                .into(binding.ivArtist)

            binding.tvArtistName.text = artist.engName
            binding.tvArtistDescription.text = artist.description

            itemView.setOnClickListener {
                onItemClick(artist)
            }
        }
    }
}

class ArtistDiffUtil : DiffUtil.ItemCallback<Artist>() {
    override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem.artistId == newItem.artistId
    }

    override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem == newItem
    }

}