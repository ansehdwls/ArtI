package com.hexa.arti.ui

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artmuseum.Subscriber
import com.hexa.arti.data.model.home.GetRecommendGalleriesResponse
import com.hexa.arti.data.model.login.LoginResponse
import com.hexa.arti.repository.ArtGalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val artGalleryRepository: ArtGalleryRepository

) : ViewModel() {
    private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val MEMBER_ID_KEY = stringPreferencesKey("member_id")
    private val GALLERY_ID_KEY = stringPreferencesKey("gallery_id")
    private val REFRESH_TOEKN_KEY = stringPreferencesKey("refresh_token")

    private val _fragmentState = MutableLiveData<Int>()
    val fragmentState: MutableLiveData<Int> = _fragmentState

    var isRecommended = false
    var recommendedData = listOf<GetRecommendGalleriesResponse>()

    private val _subscriptionGallery = MutableLiveData<List<Subscriber>>()
    val subscriptionGallery: LiveData<List<Subscriber>> = _subscriptionGallery

    var userId = 0

    fun getSubscriptionGalleries(memberId: Int) {
        Log.d("확인", "getSubscriptionGalleries: ${memberId}")
        viewModelScope.launch {
            val list = arrayListOf<Subscriber>()
            artGalleryRepository.getSubscriptionGalleries(memberId).onSuccess {
                it.forEach { data ->
                    list.add(
                        Subscriber(
                            data.galleryId,
                            data.galleryImage,
                            data.galleryName ?: "a",
                            data.ownerName,
                            data.galleryDescription ?: " ", data.viewCount
                        )
                    )
                }
                _subscriptionGallery.value = list
            }.onFailure {
                Log.d("확인", "getSubscriptionGalleries:  시;ㄹ패 $it")
            }
        }
    }

    // JWT 토큰 읽기
    fun getLoginData(): Flow<LoginResponse?> {
        return dataStore.data.map { preferences ->
            val token = preferences[JWT_TOKEN_KEY] ?: ""
            val memberId = preferences[MEMBER_ID_KEY]?.toIntOrNull() ?: -1
            val galleryId = preferences[GALLERY_ID_KEY]?.toIntOrNull() ?: -1
            val refreshToken = preferences[REFRESH_TOEKN_KEY] ?: ""
            userId = memberId
            if (token.isNotEmpty()) {
                LoginResponse(
                    token = token,
                    refreshToken = refreshToken,  // 저장하지 않은 값은 0 또는 기본값 설정
                    memberId = memberId,
                    galleryId = galleryId
                )
            } else {
                null
            }
        }
    }

    fun setFragmentState(state: Int) {
        _fragmentState.value = state
    }

    companion object {
        const val SUBSCRIBE_FRAGMENT = 3
        const val PORTFOLIO_FRAGMENT = 4
    }
}