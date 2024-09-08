package com.intern.onboardingassignment.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.domain.session.SessionManager
import com.intern.onboardingassignment.domain.usecase.GetCurrentUserDataUseCase
import com.intern.onboardingassignment.presentation.mapper.toUserModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionManager: SessionManager,
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase,
) : ViewModel() {

    private val _channel = Channel<MainAction> { }
    val channel = _channel.receiveAsFlow()

    private val _userState = MutableStateFlow<LoadUser>(LoadUser.Loading)
    val userState = _userState


    init {
        viewModelScope.launch {
            checkLoginStatus()
        }
    }

    private suspend fun checkLoginStatus() {

        val firebaseUId = try {
            sessionManager.getAuthInformation()
        } catch (e: Exception) {
            Log.e("checkLoginStatus", "Error: ${e.localizedMessage}")
            null
        }

        when {
            firebaseUId != null -> {

                getCurrentUser() // 비동기 호출
                Log.d("getUserData", "${CurrentUser.userData}")
                _channel.send(MainAction.LoggedIn)
            }

            else -> _channel.send(MainAction.NotLoggedIn)
        }
    }

    private suspend fun getCurrentUser() {
        Log.d("getCurrentUser", "실행됨?")
        getCurrentUserDataUseCase().catch { e ->
            Log.d("getCurrentUser1", "UserEntity: $e")
            _userState.value = LoadUser.Error(e)
        }.collect { userEntity ->
            Log.d("getCurrentUser", "UserEntity: $userEntity")
            val userModel = userEntity.toUserModel()
            CurrentUser.userData = userModel
            _userState.value = LoadUser.Success
        }
    }

    suspend fun signOut(){
        sessionManager.logOut()
        _channel.trySend(MainAction.LoggedIn)
    }


}

sealed interface LoadUser {

    data object Loading : LoadUser
    data object Success : LoadUser
    data class Error(val error: Throwable) : LoadUser

}

sealed interface MainAction {
    data object LoggedIn : MainAction
    data object NotLoggedIn : MainAction
}

class MainViewModelFactory(
    private val sessionManager: SessionManager,
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                sessionManager = sessionManager,
                getCurrentUserDataUseCase = getCurrentUserDataUseCase,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}