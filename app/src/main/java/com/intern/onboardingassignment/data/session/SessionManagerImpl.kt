package com.intern.onboardingassignment.data.session

import com.google.firebase.auth.FirebaseAuth
import com.intern.onboardingassignment.domain.session.FirebaseUId
import com.intern.onboardingassignment.domain.session.SessionManager

class SessionManagerImpl(private val auth: FirebaseAuth) : SessionManager {
    override suspend fun getAuthInformation(): FirebaseUId {
        return auth.uid
    }

    override suspend fun login() {

    }

    override suspend fun logOut() {
        auth.signOut()
    }

}