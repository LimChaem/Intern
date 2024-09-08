package com.intern.onboardingassignment.presentation.view.signUp

import android.os.PatternMatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.domain.session.SessionManager
import com.intern.onboardingassignment.domain.usecase.SignUpWithFirebaseUseCase
import com.intern.onboardingassignment.domain.extention.SignUpResult
import com.intern.onboardingassignment.domain.extention.SuchEmailResult
import com.intern.onboardingassignment.domain.usecase.CheckEmailDuplicateUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignUpViewModel(
    private val signUpWithFirebaseUseCase: SignUpWithFirebaseUseCase,
    private val sessionManager: SessionManager,
    private val checkEmailDuplicateUseCase: CheckEmailDuplicateUseCase,
) : ViewModel() {

    private val _channel = Channel<SignUpEvent> { }
    val channel = _channel.receiveAsFlow()

    private val _checkEmailDuplicate = Channel<CheckEmail> { }
    val checkEmailDuplicate = _checkEmailDuplicate.receiveAsFlow()

    private val _emailValid = MutableLiveData<Boolean>()
    val emailValid = _emailValid

    private val _emailValidUi = MutableLiveData<Boolean>()
    val emailValidUi = _emailValidUi

    private val _emailDuplicate = MutableLiveData<Boolean>()
    val emailDuplicate = _emailDuplicate

    private val _nameValid = MutableLiveData<Boolean>()
    val nameValid = _nameValid

    private val _nameValidUi = MutableLiveData<Boolean>()
    val nameValidUi = _nameValidUi

    private val _passwordValid = MutableLiveData<Boolean>()
    val passwordValid = _passwordValid

    private val _passwordValidUi = MutableLiveData<Boolean>()
    val passwordValidUi = _passwordValidUi

    private val _confirmPasswordValid = MutableLiveData<Boolean>()
    val confirmPasswordValid = _confirmPasswordValid

    private val _confirmPasswordValidUi = MutableLiveData<Boolean>()
    val confirmPasswordValidUi = _confirmPasswordValidUi

    private val _signUpValid = MutableLiveData<Boolean>(false)
    val signUpValid = _signUpValid


    fun checkName(name: EditText) {
        val nameText = name.text.toString()
        val namePattern = Pattern.matches("^[ㄱ-ㅣ가-힣a-zA-Z\\s]+$", nameText)
        if (nameText.isEmpty()) {
            _nameValid.value = false
            _nameValidUi.value = true
        } else {
            _nameValid.value = namePattern
            _nameValidUi.value = namePattern
        }
        updateSignUpValid()
    }

    fun checkEmail(email: EditText) {
        val emailText = email.text.toString()
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
        if (emailText.isEmpty()) {
            _emailValid.value = false
            _emailValidUi.value = true
        } else {
            _emailValid.value = emailPattern
            _emailValidUi.value = emailPattern
        }
        updateSignUpValid()
    }

    fun checkEmailDuplicate(email: EditText) {
        val emailText = email.text.toString()
        viewModelScope.launch {
            checkEmailDuplicateUseCase.invoke(emailText).collect {
                when (it) {
                    is SuchEmailResult.Empty -> {
                        _emailDuplicate.value = true
                        _checkEmailDuplicate.send(CheckEmail.Empty)
                    }
                    is SuchEmailResult.Error -> {
                        _emailDuplicate.value = false
                        _checkEmailDuplicate.send(CheckEmail.Error("다시 시도 해 주세요."))
                    }
                    is SuchEmailResult.Exits -> {
                        _emailDuplicate.value = false
                        _checkEmailDuplicate.send(CheckEmail.Exists)
                    }
                }
            }
        }
    }

    fun checkPassword(password: EditText) {
        val passwordText = password.text.toString().trim()
        val passwordPattern = Pattern.matches(
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,20}$",
            passwordText
        )
        if (passwordText.isEmpty()) {
            _passwordValid.value = false
            _passwordValidUi.value = true
        } else {
            _passwordValid.value = passwordPattern
            _passwordValidUi.value = passwordPattern
        }

        updateSignUpValid()
    }

    fun checkConfirmPassword(password: EditText, confirmPassword: EditText) {
        val passwordText = password.text.toString().trim()
        val confirmPasswordText = confirmPassword.text.toString()

        if (passwordText.isEmpty()) {
            _confirmPasswordValid.value = false
            _confirmPasswordValidUi.value = true

        } else {
            _confirmPasswordValid.value = passwordText == confirmPasswordText
            _confirmPasswordValidUi.value = passwordText == confirmPasswordText

        }
        updateSignUpValid()
    }

    private fun updateSignUpValid() {
        val allFieldsValid = (nameValid.value == true &&
                emailValid.value == true &&
                passwordValid.value == true &&
                confirmPasswordValid.value == true)
        signUpValid.value = allFieldsValid

        Log.d(
            "allFieldValid", "$allFieldsValid"
        )
    }


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

sealed interface CheckEmail {
    data object Exists : CheckEmail
    data object Empty : CheckEmail
    data class Error(val message: String) : CheckEmail
}

class SignUpViewmodelFactory(
    private val signUpWithFirebaseUseCase: SignUpWithFirebaseUseCase,
    private val sessionManager: SessionManager,
    private val checkEmailDuplicateUseCase: CheckEmailDuplicateUseCase,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(
                signUpWithFirebaseUseCase = signUpWithFirebaseUseCase,
                sessionManager = sessionManager,
                checkEmailDuplicateUseCase = checkEmailDuplicateUseCase,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}