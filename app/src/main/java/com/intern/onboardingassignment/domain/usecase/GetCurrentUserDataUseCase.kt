package com.intern.onboardingassignment.domain.usecase

import com.intern.onboardingassignment.domain.model.UserEntity
import com.intern.onboardingassignment.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserDataUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Flow<UserEntity>{
        return authRepository.getCurrentUserData()
    }
}