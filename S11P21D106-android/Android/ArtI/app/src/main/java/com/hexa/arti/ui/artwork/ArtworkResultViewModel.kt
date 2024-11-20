package com.hexa.arti.ui.artwork

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.artworkupload.ArtWorkAIDto
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.repository.ArtWorkRepository
import com.hexa.arti.repository.ArtWorkUploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ArtworkResultViewModel"

@HiltViewModel
class ArtworkResultViewModel @Inject constructor(
    private val artWorkUploadRepository: ArtWorkUploadRepository,
    private val artWorkRepository: ArtWorkRepository
) : ViewModel() {

    private val _imageUri = MutableLiveData<ByteArray>()
    val imageUri: LiveData<ByteArray> = _imageUri

    private val _artworkResult = MutableLiveData<Artwork>()
    val artworkResult: LiveData<Artwork> = _artworkResult

    private val _successStatus = MutableLiveData<Int>()
    val successStatus: LiveData<Int> = _successStatus

    fun getArtWork(id: Int) {
        viewModelScope.launch {
            artWorkRepository.getArtWorkById(id).onSuccess { response ->
                Log.d("확인", "성공 ${response}")
                _artworkResult.value = response
            }.onFailure { error ->
                if (error is ApiException) {
                    Log.d("확인", "실패 ${error.code} ${error.message}")
                    _artworkResult.value = Artwork(0, "", "", "", "")
                }
            }
        }
    }


    fun saveArtwork(themeId: Int, artworkId: Int, description: String) {
        viewModelScope.launch {
            artWorkUploadRepository.saveArtwork(
                themeId = themeId,
                artworkId = artworkId,
                description = description
            ).onSuccess {
                Log.d("확인", "saveArtwork: $it")
                _successStatus.value = 1
            }
        }
    }

    private fun saveArtworkAI(themeId: Int, artworkId: Int, description: String) {

        Log.d("확인", "saveArtwork: $themeId $artworkId $description ")

        viewModelScope.launch {
            artWorkUploadRepository.saveArtworkAI(
                themeId = themeId,
                artworkId = artworkId,
                description = description
            ).onSuccess {
                Log.d("확인", "saveArtwork: $it")
                _successStatus.value = 1
            }
        }
    }

    fun updateSuccessStatus() {
        _successStatus.value = 0
    }

    fun getAIArtworkId(artWorkAIDto: ArtWorkAIDto, themeId: Int) {
        Log.d("확인", "saveArtworkai: $artWorkAIDto $themeId")

        viewModelScope.launch {
            artWorkUploadRepository.saveAIImage(artWorkAIDto).onSuccess {
                saveArtworkAI(themeId, it.artwork_id, artWorkAIDto.ai_artwork_title)
                Log.d("확인", "saveArtworkai: $it")
            }.onFailure {
                Log.d("확인", "saveArtworkai:  ㅁ; $it")
            }
        }
    }
}