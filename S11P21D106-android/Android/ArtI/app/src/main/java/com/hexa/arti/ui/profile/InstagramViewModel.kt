package com.hexa.arti.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.repository.InstagramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstagramViewModel @Inject constructor(
    private val instagramRepository: InstagramRepository
) : ViewModel() {

    private val _resultString = MutableLiveData<String>()
    val resultString = _resultString

    fun postInstagramUrl(url: String) {
        viewModelScope.launch {
            instagramRepository.postInstagramUrl(url).onSuccess {
                _resultString.value = "인스타그램 연동 성공"
            }.onFailure {
                _resultString.value = "서버에러"
            }
        }
    }
}