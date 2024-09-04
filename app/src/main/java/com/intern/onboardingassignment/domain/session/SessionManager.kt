package com.intern.onboardingassignment.domain.session

typealias FirebaseUId = String?

interface SessionManager {
    suspend fun getAuthInformation(): FirebaseUId
    suspend fun login()
}