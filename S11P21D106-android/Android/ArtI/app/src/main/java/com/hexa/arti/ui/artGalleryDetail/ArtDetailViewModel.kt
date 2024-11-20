package com.hexa.arti.ui.artGalleryDetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artmuseum.ArtGalleryResponse
import com.hexa.arti.data.model.artmuseum.MyGalleryThemeItem
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.repository.ArtGalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtDetailViewModel @Inject constructor(
    private val galleryRepository: ArtGalleryRepository
) : ViewModel() {

    private val _galleryTheme = MutableLiveData<List<MyGalleryThemeItem>>()
    val galleryTheme : LiveData<List<MyGalleryThemeItem>> = _galleryTheme

    fun getMyGalleryTheme(galleryId: Int) {
        viewModelScope.launch {
            galleryRepository.getArtGalleryThemes(galleryId).onSuccess {
                _galleryTheme.value = it
            }.onFailure { error ->
                if (error is ApiException) {
                    Log.d("확인", "실패 ${error.code} ${error.message}")

                }
            }
        }
    }

    fun postArtwork(themeId : Int, artworkId : Int, description : String, ){
        viewModelScope.launch {
            galleryRepository.postArtworkTheme(themeId,artworkId,description).onSuccess {

            }

        }
    }
}