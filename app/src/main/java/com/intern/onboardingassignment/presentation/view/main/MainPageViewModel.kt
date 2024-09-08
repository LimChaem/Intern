package com.intern.onboardingassignment.presentation.view.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.domain.session.SessionManager
import com.intern.onboardingassignment.domain.usecase.AccountDeletionUseCase
import com.intern.onboardingassignment.domain.usecase.GetCurrentUserDataUseCase
import com.intern.onboardingassignment.presentation.CurrentUser
import com.intern.onboardingassignment.presentation.mapper.toUserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainPageViewModel(
    private val sessionManager: SessionManager,
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase,
    private val accountDeletionUseCase: AccountDeletionUseCase,
) : ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val loadingState = _loadingState

    private val _sessionEvent = MutableStateFlow<SessionEvent>(SessionEvent.LoggedIn)
    val sessionEvent = _sessionEvent

     suspend fun getCurrentUser() {
        val currentUser = CurrentUser.userData
        if (currentUser == null) {
            getCurrentUserDataUseCase().catch { e ->
                Log.d("getCurrentUser1", "UserEntity: $e")
                _loadingState.value = LoadingState.Error(e)
            }.collect { userEntity ->
                Log.d("getCurrentUser", "UserEntity: $userEntity")
                val userModel = userEntity.toUserModel()
                CurrentUser.userData = userModel
                _loadingState.value = LoadingState.Success
            }
        }else{
            _loadingState.value = LoadingState.Success
        }
    }

    suspend fun logOut(){
        sessionManager.logOut()
        _sessionEvent.value = SessionEvent.LoggedOut
    }

    fun accountDeletion(){
       viewModelScope.launch {
           accountDeletionUseCase.invoke().collect{success ->
               if(success){
                   _sessionEvent.value = SessionEvent.DeleteUserData
               }else {
                   _sessionEvent.value = SessionEvent.FailedDeleteData
               }
           }
       }
    }
}

sealed interface LoadingState {
    data object Loading : LoadingState
    data object Success : LoadingState
    data class Error(val error: Throwable) : LoadingState
}

sealed interface SessionEvent {

    data object DeleteUserData: SessionEvent
    data object FailedDeleteData: SessionEvent
    data object LoggedOut: SessionEvent
    data object LoggedIn: SessionEvent
}

class MainPageViewModelFactory(
    private val sessionManager: SessionManager,
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase,
    private val accountDeletionUseCase: AccountDeletionUseCase,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainPageViewModel(
                sessionManager = sessionManager,
                getCurrentUserDataUseCase = getCurrentUserDataUseCase,
                accountDeletionUseCase = accountDeletionUseCase,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}