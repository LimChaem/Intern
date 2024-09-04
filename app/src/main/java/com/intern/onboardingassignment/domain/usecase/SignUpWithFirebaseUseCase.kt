package com.intern.onboardingassignment.domain.usecase

import com.intern.onboardingassignment.domain.repository.AuthRepository
import com.intern.onboardingassignment.domain.utill.SignUpResult
import kotlinx.coroutines.flow.Flow

class SignUpWithFirebaseUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name:String, email: String, password: String): Flow<SignUpResult> {
        return authRepository.signUp(name = name, email = email, password = password)
    }
}