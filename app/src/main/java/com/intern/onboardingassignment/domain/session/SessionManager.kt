package com.intern.onboardingassignment.domain.session

import com.intern.onboardingassignment.domain.extention.LoginResult
import kotlinx.coroutines.flow.Flow

typealias FirebaseUId = String?

interface SessionManager {
    suspend fun getAuthInformation(): FirebaseUId
    suspend fun loginWithFirebase(email:String, password:String): Flow<LoginResult>
    suspend fun logOut()
}