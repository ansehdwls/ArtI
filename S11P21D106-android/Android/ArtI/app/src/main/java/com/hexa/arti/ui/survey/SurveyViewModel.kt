package com.hexa.arti.ui.survey

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.survey.SurveyResponse
import com.hexa.arti.data.model.survey.SurveyResponseItem
import com.hexa.arti.repository.ArtWorkUploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val artWorkUploadRepository: ArtWorkUploadRepository
) : ViewModel() {

    private val _surveyImage = MutableLiveData<Array<Array<SurveyResponseItem>>>()
    val surveyImage : LiveData<Array<Array<SurveyResponseItem>>> = _surveyImage


    fun getSurveyImage(){
        viewModelScope.launch {
            artWorkUploadRepository.getSurveyImage().onSuccess {
                Log.d("확인", "getSurveyImage: $it")

                _surveyImage.value = it.chunked(9).map { data ->
                    data.toTypedArray()
                }.toTypedArray()
            }.onFailure {
                Log.d("확인", "ㄷerror: $it")
            }
        }
    }

}