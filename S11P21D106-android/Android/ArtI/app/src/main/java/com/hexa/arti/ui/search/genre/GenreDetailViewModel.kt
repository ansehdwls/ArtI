package com.hexa.arti.ui.search.genre

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.repository.ArtGalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreDetailViewModel @Inject constructor(
    private val artGalleryRepository: ArtGalleryRepository
) : ViewModel() {

    private val _resultArtworks = MutableLiveData<List<Artwork>>()
    val resultArtwork = _resultArtworks

    fun getGenreRandomData(genreName: String) {
        viewModelScope.launch {
            artGalleryRepository.getRandomGenreArtworks(genreName).onSuccess { response ->
                _resultArtworks.value = response
            }
                .onFailure { error ->
                    if (error is ApiException) {
                        _resultArtworks.value = emptyList()
                    }
                }
        }
    }


}