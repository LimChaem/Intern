package com.intern.onboardingassignment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.domain.session.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _channel = Channel<MainAction> { }
    val channel = _channel.receiveAsFlow()


    init {
        viewModelScope.launch {
            checkLoginStatus()
        }
    }

    private suspend fun checkLoginStatus() {

        val firebaseUId = try {
            sessionManager.getAuthInformation()
        } catch (e: Exception) {
            null
        }

        when {
            firebaseUId != null -> _channel.send(MainAction.LoggedIn)
            else -> _channel.send(MainAction.NotLoggedIn)
        }
    }

}

sealed interface MainAction {
    data object LoggedIn : MainAction
    data object NotLoggedIn : MainAction
}

class MainViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}