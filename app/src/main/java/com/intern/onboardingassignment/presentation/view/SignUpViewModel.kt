package com.intern.onboardingassignment.presentation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.domain.usecase.SignUpWithFirebaseUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val signUpWithFirebaseUseCase: SignUpWithFirebaseUseCase,
) : ViewModel() {

    private val _channel = Channel<SignUpEvent> { }
    val channel = _channel.receiveAsFlow()


    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = signUpWithFirebaseUseCase(name = name, email = email, password = password)
        }

    }

}

sealed interface SignUpEvent {
    data object SignUpSuccess : SignUpEvent
    data class SignUpFail(val message: String) : SignUpEvent
}

class SignUpViewmodelFactory(
    private val signUpWithFirebaseUseCase: SignUpWithFirebaseUseCase,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(
                signUpWithFirebaseUseCase,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}