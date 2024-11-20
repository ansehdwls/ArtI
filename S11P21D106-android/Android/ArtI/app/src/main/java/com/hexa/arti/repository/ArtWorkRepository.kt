package com.hexa.arti.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.hexa.arti.data.model.artwork.Artwork
import kotlinx.coroutines.flow.Flow

interface ArtWorkRepository {
    suspend fun getArtWorkById(artworkId: Int): Result<Artwork>

    suspend fun getArtWorksByString(keyword: String): Result<List<Artwork>>

    suspend fun getArtWorksByStringWithPaging(keyword: String): Flow<PagingData<Artwork>>
}