package com.hexa.arti.ui.survey

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artmuseum.CreateThemeDto
import com.hexa.arti.data.model.artmuseum.GalleryRequest
import com.hexa.arti.data.model.artmuseum.GalleryResponse
import com.hexa.arti.data.model.artmuseum.ThemeResponseItem
import com.hexa.arti.data.model.survey.SurveyListDto
import com.hexa.arti.network.ArtWorkUpload
import com.hexa.arti.repository.ArtGalleryRepository
import com.hexa.arti.repository.ArtWorkUploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CreateGalleryViewModel @Inject constructor(
    private val galleryRepository: ArtGalleryRepository,
    private val artWorkUploadRepository: ArtWorkUploadRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _thumbnail = MutableLiveData<MultipartBody.Part>()
    val thumbnail : LiveData<MultipartBody.Part> = _thumbnail

    private val _galleryResponse = MutableLiveData<GalleryResponse>()
    val galleryResponse : LiveData<GalleryResponse> = _galleryResponse

    private val _themeDto = MutableLiveData<ThemeResponseItem>()
    val themeDto : LiveData<ThemeResponseItem> = _themeDto
    private val GALLERY_ID_KEY = stringPreferencesKey("gallery_id")

    fun createGallery(galleryDto : GalleryRequest){
        viewModelScope.launch {
            Log.d("확인", "createGallery: ${galleryDto}")
            galleryRepository.postGallery(_thumbnail.value!!,galleryDto).onSuccess {
                Log.d("확인", "createGallery: ${galleryDto}")
                saveGalleryData(it.id)
                _galleryResponse.value = it
                Log.d("확인", "galleryRepository: $it")
            }.onFailure {
                Log.d("확인", "galleryRepository: $it")
            }
        }

    }

    fun createTheme(id : Int, theme : String){
        Log.d("확인", "createTheme: $id $theme")
        viewModelScope.launch {
            galleryRepository.postTheme(CreateThemeDto(id,theme)).onSuccess {
                _themeDto.value = it
                Log.d("확인", "galleryRepository: $it")
            }.onFailure {
                Log.d("확인", "errrr galleryRepository: $it")
            }
        }
    }

    fun updateThumbnail(image :MultipartBody.Part){
        _thumbnail.value = image
    }

    fun saveSurvey(surveyListDto: SurveyListDto){
        viewModelScope.launch {
            artWorkUploadRepository.saveSurvey(surveyListDto).onSuccess {

            }.onFailure {

            }
        }
    }

    suspend fun saveGalleryData(galleryId: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[GALLERY_ID_KEY] = galleryId.toString()  // Int를 String으로 변환하여 저장
            }
        }
    }

    fun updateId(){
        _themeDto.value = ThemeResponseItem(id = 0, name = "", galleryName = "")
    }
}