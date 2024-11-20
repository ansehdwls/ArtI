package com.hexa.arti.ui.artwork

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.repository.ArtWorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SelectArtworkViewModel"
@HiltViewModel
class SelectArtworkViewModel @Inject constructor(
    private val artWorkRepository: ArtWorkRepository
) : ViewModel() {

    private val _artworkResult = MutableStateFlow<PagingData<Artwork>>(PagingData.empty())
    val artWorkResult: StateFlow<PagingData<Artwork>> = _artworkResult.asStateFlow()

    fun getArtworkByString(keyword: String) {
        viewModelScope.launch {
            artWorkRepository.getArtWorksByStringWithPaging(keyword)
                .cachedIn(viewModelScope)
                .collect {
                    _artworkResult.value = it
                }
        }
    }

}