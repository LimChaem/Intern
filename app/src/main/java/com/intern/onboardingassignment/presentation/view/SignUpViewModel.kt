package com.intern.onboardingassignment.presentation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.domain.session.SessionManager
import com.intern.onboardingassignment.domain.usecase.SignUpWithFirebaseUseCase
import com.intern.onboardingassignment.domain.utill.SignUpResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val signUpWithFirebaseUseCase: SignUpWithFirebaseUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _channel = Channel<SignUpEvent> { }
    val channel = _channel.receiveAsFlow()


    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            signUpWithFirebaseUseCase(
                name = name,
                email = email,
                password = password
            ).collect { result ->

                when (result) {
                    is SignUpResult.Success -> {
                        sessionManager.logOut()
                        _channel.send(SignUpEvent.SignUpSuccess)

                    }

                    is SignUpResult.Failure -> {
                        _channel.send(SignUpEvent.SignUpFail(result.message))
                    }

                    SignUpResult.InvalidEmailError -> {
                        _channel.send(SignUpEvent.SignUpFail("이메일 오류"))
                    }

                    SignUpResult.NetworkError -> {
                        _channel.send(SignUpEvent.SignUpFail("네트워크 통신 오류 입니다. 잠시 후에 다시 시도 해 주세요."))
                    }

                    SignUpResult.UserCollisionError -> {
                        _channel.send(SignUpEvent.SignUpFail("이미 가입된 회원 입니다."))
                    }
                    SignUpResult.WeakPasswordError -> {
                        _channel.send(SignUpEvent.SignUpFail("비밀번호가 취약합니다."))
                    }
                }
            }
        }

    }

}

sealed interface SignUpEvent {
    data object SignUpSuccess : SignUpEvent
    data class SignUpFail(val message: String) : SignUpEvent
}

class SignUpViewmodelFactory(
    private val signUpWithFirebaseUseCase: SignUpWithFirebaseUseCase,
    private val sessionManager: SessionManager,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(
                signUpWithFirebaseUseCase = signUpWithFirebaseUseCase,
                sessionManager = sessionManager,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}