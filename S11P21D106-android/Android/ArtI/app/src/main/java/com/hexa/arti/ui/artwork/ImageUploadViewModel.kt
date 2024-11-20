package com.hexa.arti.ui.artwork

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.network.ArtWorkUpload
import com.hexa.arti.repository.ArtWorkRepository
import com.hexa.arti.repository.ArtWorkUploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

private const val TAG = "ImageUploadViewModel"

@HiltViewModel
class ImageUploadViewModel @Inject constructor(
    private val artWorkUploadRepository: ArtWorkUploadRepository,
    private val artWorkRepository: ArtWorkRepository
) : ViewModel() {


    private val _imageResponse = MutableLiveData<String>()
    val imageResponse: LiveData<String> = _imageResponse

    private val _artworkResult = MutableLiveData<Artwork>()
    val artworkResult: LiveData<Artwork> = _artworkResult

    fun makeImage(contentImage : MultipartBody.Part, styleImage : Int)
    {
        Log.d(TAG, "makeImage: ${styleImage}")
        Log.d(TAG, "makeImage: ${makeStyleImageIdPart(styleImage)}")


        viewModelScope.launch {
            artWorkUploadRepository.postMakeAIImage(contentImage = contentImage, styleImage = makeStyleImageIdPart(styleImage)).onSuccess {
                    response ->
                Log.d(TAG, "ImageUploadViewModel: ${response}")
                _imageResponse.value = response.image_url

            }.onFailure {
                    error ->
                Log.d(TAG, "ImageUploadViewModel: ${error}")
            }
        }
    }

    private fun makeStyleImageIdPart(styleImageId: Int): RequestBody {
        return styleImageId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun getArtWork(id : Int){
        viewModelScope.launch {
            artWorkRepository.getArtWorkById(id).onSuccess { response ->
                Log.d("확인", "성공 ${response}")
                _artworkResult.value = response
            }.onFailure { error ->
                if (error is ApiException) {
                    Log.d("확인", "실패 ${error.code} ${error.message}")
                    _artworkResult.value = Artwork(0,"","","","")
                }
            }
        }
    }
    fun updateImageResponse(){
        _imageResponse.value = ""
    }
}