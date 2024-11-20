package com.hexa.arti.ui.artmuseum

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artmuseum.CreateThemeDto
import com.hexa.arti.data.model.artmuseum.GalleryRequest
import com.hexa.arti.data.model.artmuseum.ThemeResponseItem
import com.hexa.arti.data.model.artmuseum.UpdateGalleryDto
import com.hexa.arti.repository.ArtGalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

private const val TAG = "MyGalleryViewModel"
@HiltViewModel
class MyGalleryViewModel @Inject constructor(
    private val galleryRepository: ArtGalleryRepository
): ViewModel() {

    private val _updateGalleryDto  = MutableLiveData<GalleryRequest>()
    val updateGalleryDto : LiveData<GalleryRequest> = _updateGalleryDto

    private val _thumbnail = MutableLiveData<MultipartBody.Part>()
    val thumbnail : LiveData<MultipartBody.Part> = _thumbnail

    private val _updateThemeDto = MutableLiveData<ThemeResponseItem>()
    val updateThemeDto : LiveData<ThemeResponseItem> = _updateThemeDto

    fun createTheme(themeDto: CreateThemeDto){
        Log.d(TAG, "Init:  ${themeDto} ")
        viewModelScope.launch {
            galleryRepository.postTheme(themeDto).onSuccess {
                    response ->
                _updateThemeDto.value = response
                Log.d("Init", "$response deleteThemeDelete: success")
            }.onFailure {
                    error ->
                Log.d("Init", "$error deleteThemeDelete: fail")
            }
        }
    }

    fun updateGallery(galleryId : Int){
        viewModelScope.launch {
            _thumbnail.value = _thumbnail.value ?: createEmptyImagePart("image")
            galleryRepository.updateArtGallery(galleryId,_thumbnail.value!!,_updateGalleryDto.value!!)
        }

    }

    private fun createEmptyImagePart(name: String): MultipartBody.Part {
        val emptyData = ByteArray(0)

        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), emptyData)

        return MultipartBody.Part.createFormData(name, "empty_image.png", requestBody)
    }

    fun deleteTheme(galleryId: Int, themeId: Int){
        viewModelScope.launch {
            galleryRepository.deleteTheme(galleryId,themeId).onSuccess {
                    response ->
                Log.d("Init", "$response deleteThemeDelete: success")
            }.onFailure {
                    error ->
                Log.d("Init", "$error deleteThemeDelete: fail")
            }
        }
    }
    fun deleteThemeDelete(themeId: Int, artworkId: Int){
        viewModelScope.launch {
            galleryRepository.deleteThemeArtWork(themeId,artworkId).onSuccess {
                response ->
                Log.d("Init", "$response deleteThemeDelete: success")
            }.onFailure {
                error ->
                Log.d("Init", "$error deleteThemeDelete: fail")
            }
        }
    }


    fun getGalleryDto(updateGalleryDto: GalleryRequest){
        Log.d(TAG, "getGalleryDto: ${updateGalleryDto}")
        _updateGalleryDto.value = updateGalleryDto
    }
    fun getImage(image : MultipartBody.Part){

        _thumbnail.value = image
    }

    fun updateGalleryName(name : String,galleryId : Int){
        _updateGalleryDto.value = _updateGalleryDto.value?.copy(name = name)
        Log.d(TAG, "updateGalleryName: ${name} ${galleryId}")
        updateGallery(galleryId)
    }
    fun updateThumbnail(image :MultipartBody.Part,galleryId: Int){
        _thumbnail.value = image
        updateGallery(galleryId)
    }
    fun updateGalleryDescription(description : String,galleryId : Int){
        _updateGalleryDto.value = _updateGalleryDto.value?.copy(description = description)
        updateGallery(galleryId)
    }

}