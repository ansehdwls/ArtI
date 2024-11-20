package com.hexa.arti.ui.artmuseum

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.artmuseum.Subscriber
import com.hexa.arti.data.model.artmuseum.SubscriptionGallery
import com.hexa.arti.repository.ArtGalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscribeViewModel @Inject constructor(
    private val artGalleryRepository: ArtGalleryRepository
) : ViewModel()
{

    private val _subscriptionGallery = MutableLiveData<List<Subscriber>>()
    val subscriptionGallery : LiveData<List<Subscriber>> = _subscriptionGallery

    fun getSubscriptionGalleries(memberId : Int){
        Log.d("확인", "getSubscriptionGalleries: ${memberId}")
        viewModelScope.launch {
            val list = arrayListOf <Subscriber>()
            artGalleryRepository.getSubscriptionGalleries(memberId).onSuccess {
                Log.d("확인", "getSubscriptionGalleries:  성공 $it")
                it.forEach{
                    data ->
                    list.add(
                        Subscriber(data.galleryId,
                            data.galleryImage,
                            data.galleryName ?: "a",
                            data.ownerName ,
                            data.galleryDescription ?: " "
                            ,data.viewCount)
                    )
                }
                _subscriptionGallery.value = list
            }.onFailure {
                Log.d("확인", "getSubscriptionGalleries:  시;ㄹ패 $it")
            }
        }
    }
}