package com.hexa.arti.ui.search.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.network.ArtWorkApi
import com.hexa.arti.util.asArtwork

class ArtworkPagingSource(
    private val artWorkApi: ArtWorkApi,
    private val keyword: String
) : PagingSource<Int, Artwork>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artwork> {
        val pageNumber = params.key ?: 1
        return try {
            val response = artWorkApi.getArtworksByStringWithPaging(pageNumber,keyword)
            val artWorks = response.artWorks.map { it.asArtwork() }
            LoadResult.Page(
                data = artWorks,
                prevKey = if (pageNumber == 1) null else pageNumber - 1,
                nextKey = if (pageNumber == response.totalPages) null else pageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Page(
                data = emptyList(),
                prevKey = if (pageNumber == 1) null else pageNumber - 1,
                nextKey = if (pageNumber == 60) null else pageNumber + 1
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Artwork>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}