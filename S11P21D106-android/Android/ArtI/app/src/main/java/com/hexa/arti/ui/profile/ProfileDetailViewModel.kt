package com.hexa.arti.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.repository.MemberRepository
import com.hexa.arti.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _successStatus = MutableLiveData<Int>()
    val successStatus : LiveData<Int> = _successStatus


    fun changeNickname(nickname: String){
        viewModelScope.launch {
            userRepository.postChangeNickname(nickname).onSuccess {
                Log.d("확인", "changeNickname: $it")
                _successStatus.value = 1
            }.onFailure {
                _successStatus.value = 2
            }
        }
    }


    fun changePass(currentPassword : String,newPassword : String, confirmationPassword : String){
        viewModelScope.launch {
            userRepository.postChangePass(currentPassword = currentPassword, newPassword = newPassword, confirmationPassword = confirmationPassword).onSuccess {
                Log.d("확인", "changePass: $it")
                _successStatus.value = 3
            }.onFailure {
                _successStatus.value = 2
            }
        }
    }
    fun updateSuccessStatus(){
        _successStatus.value = 0
    }
}