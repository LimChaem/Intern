package com.intern.onboardingassignment.presentation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.domain.session.SessionManager
import com.intern.onboardingassignment.domain.usecase.LogInWithFirebaseUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _channel = Channel<LogInEvent> {  }
    val channel = _channel.receiveAsFlow()

    fun LogInWithFirebase(email:String, password: String){
        viewModelScope.launch {
            log
        }
    }


    sealed interface LogInEvent{
        object LogInSuccess: LogInEvent
        data object LogInFail: LogInEvent
    }
}

class LoginViewModelFactory(
    private val sessionManager: SessionManager,
) :
    ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                sessionManager,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}