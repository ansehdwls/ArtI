package com.hexa.arti.ui.survey

import androidx.lifecycle.ViewModel

class InstagramSurveyViewModel():ViewModel() {
    var status = 1

    fun updateStatus(){
        status = 2
    }
}