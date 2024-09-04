package com.intern.onboardingassignment.presentation.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainPageViewModel : ViewModel() {

}


class MainPageViewModelFactory() :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainPageViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}