package com.intern.onboardingassignment.domain.usecase

import com.intern.onboardingassignment.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AccountDeletionUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Flow<Boolean> {
        return authRepository.accountDeletion()
    }
}