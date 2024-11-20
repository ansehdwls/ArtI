package com.hexa.arti.ui

import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artmuseum.ArtGalleryResponse
import com.hexa.arti.data.model.artmuseum.MyGalleryThemeItem
import com.hexa.arti.data.model.artwork.Artwork
import com.hexa.arti.data.model.response.ApiException
import com.hexa.arti.network.GalleryApi
import com.hexa.arti.repository.ArtGalleryRepository
import com.hexa.arti.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyGalleryActivityViewModel @Inject constructor(
    private val galleryRepository: ArtGalleryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nickName = MutableLiveData<String>()
    val nickname : LiveData<String> = _nickName

    private val _myGallery = MutableLiveData<ArtGalleryResponse>()
    val myGallery: LiveData<ArtGalleryResponse> = _myGallery

    private val _myGalleryTheme = MutableLiveData<List<MyGalleryThemeItem>>()
    val myGalleryTheme: LiveData<List<MyGalleryThemeItem>> = _myGalleryTheme

    fun getMyGallery(gallery: Int) {
        viewModelScope.launch {
            galleryRepository.getArtGallery(gallery).onSuccess {
                _myGallery.value = it
            }.onFailure { error ->
                if (error is ApiException) {
                    Log.d("확인", "실패 ${error.code} ${error.message}")
                    _myGallery.value = ArtGalleryResponse("", 0, "", "", 0, 0)
                }
            }
        }
    }

    fun getMyGalleryTheme(galleryId: Int) {
        viewModelScope.launch {
            galleryRepository.getArtGalleryThemes(galleryId).onSuccess {
                _myGalleryTheme.value = it
            }.onFailure { error ->
                if (error is ApiException) {
                    Log.d("확인", "실패 ${error.code} ${error.message}")
                }

            }
        }
    }
    fun getNickName(){
        viewModelScope.launch {
            userRepository.getUserNickname().onSuccess {
                _nickName.value =  it
            }
        }
    }
    fun updateNickname(nickname: String){
        _nickName.value =  nickname
    }

}