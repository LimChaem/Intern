package com.intern.onboardingassignment.data.dataSource

import com.google.firebase.auth.FirebaseUser
import com.intern.onboardingassignment.data.model.UserResponse
import com.intern.onboardingassignment.domain.extention.SuchEmailResult
import kotlinx.coroutines.flow.Flow

interface FirebaseDataSource {
    suspend fun signUpWithFirebase(email: String, password: String): Flow<FirebaseUser>
    suspend fun addUserDataToFirebase(name: String, email: String, uId:String)
    suspend fun checkDuplicateEmail(email:String): Flow<SuchEmailResult>

    suspend fun getCurrentUserData(): Flow<UserResponse>
    suspend fun accountDeletionWithFirebase(): Flow<Boolean>

    suspend fun deleteUserData(uId: String)
}