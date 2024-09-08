package com.intern.onboardingassignment.domain.usecase

import com.intern.onboardingassignment.domain.extention.SuchEmailResult
import com.intern.onboardingassignment.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class CheckEmailDuplicateUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): Flow<SuchEmailResult> {
        return authRepository.checkEmailDuplicate(email)
    }
}