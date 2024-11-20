package com.hexa.arti.ui.login

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexa.arti.data.model.login.LoginResponse
import com.hexa.arti.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val MEMBER_ID_KEY = stringPreferencesKey("member_id")
    private val GALLERY_ID_KEY = stringPreferencesKey("gallery_id")
    private val REFRESH_TOEKN_KEY = stringPreferencesKey("refresh_token")

    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> = _email

    private val _pass = MutableLiveData<String>("")
    val pass: LiveData<String> = _pass

    private val _loginStatus = MutableLiveData<Int>()
    val loginStatus: LiveData<Int> = _loginStatus

    fun login() {
        viewModelScope.launch {
            loginRepository.postLogin(_email.value.toString(), _pass.value.toString()).onSuccess {
                response ->
                Log.d(TAG, "login: ${response}")
                Log.d(TAG, "login: ${response.token}")
                saveLoginData(response.token,response.memberId,response.galleryId,response.refreshToken)
                _loginStatus.value = 1
            }.onFailure {
                error ->
                Log.d(TAG, "login: ${error}")
                _loginStatus.value = 2
            }
        }
    }

    // JWT 토큰 저장
    suspend fun saveLoginData(token: String, memberId: Int, galleryId: Int, refreshToken : String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[JWT_TOKEN_KEY] = token
                preferences[MEMBER_ID_KEY] = memberId.toString()  // Int를 String으로 변환하여 저장
                preferences[GALLERY_ID_KEY] = galleryId.toString()  // Int를 String으로 변환하여 저장
                preferences[REFRESH_TOEKN_KEY] = refreshToken
            }
        }
    }

    fun getLoginData(): Flow<LoginResponse?> {
        return dataStore.data.map { preferences ->
            val token = preferences[JWT_TOKEN_KEY] ?: ""
            val memberId = preferences[MEMBER_ID_KEY]?.toIntOrNull() ?: -1
            val galleryId = preferences[GALLERY_ID_KEY]?.toIntOrNull() ?: -1
            val refreshToken = preferences[REFRESH_TOEKN_KEY] ?: ""

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

    fun updateEmail(em: String) {
        _email.value = em
    }

    fun updatePass(pw: String) {
        _pass.value = pw
    }
    fun loginReset(){
        _loginStatus.value = 0
    }
}