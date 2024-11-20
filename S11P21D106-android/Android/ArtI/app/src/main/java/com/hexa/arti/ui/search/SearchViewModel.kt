package com.hexa.arti.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.data.model.response.GetSearchGalleryResponse
import com.hexa.arti.data.model.search.Artist
import com.hexa.arti.repository.ArtGalleryRepository
import com.hexa.arti.repository.ArtWorkRepository
import com.hexa.arti.repository.ArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val artWorkRepository: ArtWorkRepository,
    private val artistRepository: ArtistRepository,
    private val artGalleryRepository: ArtGalleryRepository,
) : ViewModel() {
    var state = SearchFragment.BASE_STATE

    private val _artistResult = MutableLiveData<List<Artist>>()
    val artistResult: LiveData<List<Artist>> = _artistResult

    private val _artworkResult = MutableStateFlow<PagingData<Artwork>>(PagingData.empty())
    val artWorkResult: StateFlow<PagingData<Artwork>> = _artworkResult.asStateFlow()

    private val _galleriesResult = MutableLiveData<List<GetSearchGalleryResponse>>()
    val galleriesResult = _galleriesResult

    fun getSearchGalleries(keyword: String) {
        viewModelScope.launch {
            artGalleryRepository.getSearchGalleries(keyword).onSuccess {
                _galleriesResult.value = it
            }.onFailure { error ->
                if (error is ApiException) {
                    _galleriesResult.value = emptyList()
                }
            }
        }
    }

    fun getArtworkByString(keyword: String) {
        viewModelScope.launch {
            artWorkRepository.getArtWorksByStringWithPaging(keyword)
                .cachedIn(viewModelScope)
                .collect {
                    _artworkResult.value = it
                }
        }
    }

    fun getArtistByString(keyword: String) {
        viewModelScope.launch {
            artistRepository.getArtist(keyword).onSuccess { response ->
                _artistResult.value = response
            }.onFailure { error ->
                if (error is ApiException) {
                    _artistResult.value = emptyList()
                }
            }
        }
    }

}