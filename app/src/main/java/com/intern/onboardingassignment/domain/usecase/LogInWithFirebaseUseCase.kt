package com.intern.onboardingassignment.domain.usecase

import com.intern.onboardingassignment.domain.session.SessionManager
import kotlinx.coroutines.flow.Flow

class LogInWithFirebaseUseCase(private val sessionManager: SessionManager) {
    suspend operator fun invoke(email:String, password:String): Flow<> {

    }
}