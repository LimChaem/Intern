package com.intern.onboardingassignment.domain.repository

import com.intern.onboardingassignment.domain.extention.SignUpResult
import kotlinx.coroutines.flow.Flow


interface AuthRepository {
    suspend fun signUp(name:String, email: String, password:String): Flow<SignUpResult>
}