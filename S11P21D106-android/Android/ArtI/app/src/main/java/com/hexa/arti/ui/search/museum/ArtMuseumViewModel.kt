package com.hexa.arti.ui.search.museum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artmuseum.GetTotalThemeResponse
import com.hexa.arti.data.model.artmuseum.Subscriber
import com.hexa.arti.repository.ArtGalleryRepository
import com.hexa.arti.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtMuseumViewModel @Inject constructor(
    private val galleryRepository: ArtGalleryRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _resultTotalTheme = MutableLiveData<List<GetTotalThemeResponse>>()
    val resultTotalTheme = _resultTotalTheme

    private val _subscriptionGallery = MutableLiveData<List<Subscriber>>()
    val subscriptionGallery: LiveData<List<Subscriber>> = _subscriptionGallery

    private val _subscribeResult = MutableLiveData<String>()
    val subscribeResult = _subscribeResult


    fun getSubscriptionGalleries(memberId: Int) {
        viewModelScope.launch {
            val list = arrayListOf<Subscriber>()
            galleryRepository.getSubscriptionGalleries(memberId).onSuccess {
                it.forEach { data ->
                    list.add(
                        Subscriber(
                            data.galleryId,
                            data.galleryImage,
                            data.galleryName,
                            data.ownerName,
                            data.galleryDescription ?: "ㅁㅁ",
                            data.viewCount
                        )
                    )
                }
                _subscriptionGallery.value = list
            }.onFailure {

            }
        }
    }

    fun getTotalThemes(galleryId: Int) {
        viewModelScope.launch {
            galleryRepository.getThemeWithArtworks(galleryId).onSuccess {
                _resultTotalTheme.value = it
            }.onFailure {
                _resultTotalTheme.value = emptyList()
            }
        }
    }

    fun subscribe(memberId: Int, galleryId: Int) {
        viewModelScope.launch {
            memberRepository.postSubscribe(memberId, galleryId).onSuccess {
                _subscribeResult.value = it.message
            }.onFailure {
                _subscribeResult.value = "네트워크 오류! 구독 실패"
            }
        }
    }

    fun unSubscribe(memberId: Int, galleryId: Int) {
        viewModelScope.launch {
            memberRepository.postUnsubscribe(memberId, galleryId).onSuccess {
                _subscribeResult.value = it.message
            }.onFailure {
                _subscribeResult.value = "네트워크 오류! 구독 취소 실패"
            }
        }
    }


}