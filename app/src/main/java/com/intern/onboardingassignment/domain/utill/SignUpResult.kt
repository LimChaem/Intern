package com.intern.onboardingassignment.domain.utill

sealed class SignUpResult {
    object Success: SignUpResult()
    data class Failure(val message: String): SignUpResult()

    object NetworkError : SignUpResult()
    object InvalidEmailError : SignUpResult()
    object WeakPasswordError : SignUpResult()
    object UserCollisionError : SignUpResult()

}