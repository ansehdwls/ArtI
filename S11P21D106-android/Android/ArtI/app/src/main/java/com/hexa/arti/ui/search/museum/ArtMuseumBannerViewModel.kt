package com.hexa.arti.ui.search.museum

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artmuseum.GalleryBanner
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.repository.ArtGalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtMuseumBannerViewModel @Inject constructor(
    private val artGalleryRepository: ArtGalleryRepository
) : ViewModel() {

    private val _resultMuseums = MutableLiveData<List<GalleryBanner>>()
    val resultMuseums = _resultMuseums

    fun getRandomMuseums() {
        viewModelScope.launch {
            artGalleryRepository.getRandomGalleries().onSuccess { response ->
                _resultMuseums.value = response
            }.onFailure { error ->
                if (error is ApiException) {
                    _resultMuseums.value = emptyList()
                }
            }
        }
    }

}