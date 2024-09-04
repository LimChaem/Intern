package com.intern.onboardingassignment.presentation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.intern.onboardingassignment.domain.session.SessionManager

class LoginViewModel(
    private val sessionManager: SessionManager,
) : ViewModel() {


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