package com.intern.onboardingassignment.domain.extention

sealed class LoginResult {
    object Success : LoginResult()
    data class Fail(val message: String) : LoginResult()

    object InvalidEmailError : LoginResult()
    object WrongPasswordError : LoginResult()
    object UserNotFoundError : LoginResult()
    object UserDisabledError : LoginResult()
}