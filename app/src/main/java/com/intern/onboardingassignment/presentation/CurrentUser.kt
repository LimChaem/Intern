package com.intern.onboardingassignment.presentation

import com.intern.onboardingassignment.presentation.model.UserModel

object CurrentUser {
    var userData: UserModel? = null

    fun clearData(){
        userData = null
    }
}