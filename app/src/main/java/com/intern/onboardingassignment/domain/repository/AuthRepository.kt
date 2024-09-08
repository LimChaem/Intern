package com.intern.onboardingassignment.domain.repository

import com.intern.onboardingassignment.domain.extention.SignUpResult
import com.intern.onboardingassignment.domain.extention.SuchEmailResult
import com.intern.onboardingassignment.domain.model.UserEntity
import kotlinx.coroutines.flow.Flow


interface AuthRepository {
    suspend fun signUp(name:String, email: String, password:String): Flow<SignUpResult>
    suspend fun checkEmailDuplicate(email: String): Flow<SuchEmailResult>

    suspend fun getCurrentUserData(): Flow<UserEntity>

    suspend fun accountDeletion(): Flow<Boolean>
}