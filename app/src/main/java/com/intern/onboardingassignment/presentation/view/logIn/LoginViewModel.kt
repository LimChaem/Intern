package com.intern.onboardingassignment.presentation.view.logIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.domain.session.SessionManager
import com.intern.onboardingassignment.domain.extention.LoginResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _channel = Channel<LogInEvent> { }
    val channel = _channel.receiveAsFlow()

    fun logInWithFirebase(email: String, password: String) {

        if (email.isEmpty()) {
            _channel.trySend(LogInEvent.LogInFail("E-Mail을 입력해주세요."))
            return
        }

        if (password.isEmpty()) {
            _channel.trySend(LogInEvent.LogInFail("비밀번호를 입력해주세요."))
            return
        }

        viewModelScope.launch {
            sessionManager.loginWithFirebase(email, password).collect {
                    when (it) {
                        is LoginResult.Success -> _channel.send(LogInEvent.LogInSuccess)
                        is LoginResult.Fail -> _channel.send(LogInEvent.LogInFail(it.message))
                        LoginResult.InvalidEmailError -> _channel.send(LogInEvent.LogInFail("E-Mail 형식이 맞지 않습니다."))
                        LoginResult.WrongPasswordError -> _channel.send(LogInEvent.LogInFail("비밀번호를 확인 해 주세요."))
                        LoginResult.UserDisabledError -> _channel.send(LogInEvent.LogInFail("사용이 중지 된 계정 입니다."))
                        LoginResult.UserNotFoundError -> _channel.send(LogInEvent.LogInFail("가입되지 않은 E-Mail입니다."))
                    }


            }
        }
    }
}

sealed interface LogInEvent {
    data object LogInSuccess : LogInEvent
    data class LogInFail(val message: String) : LogInEvent

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