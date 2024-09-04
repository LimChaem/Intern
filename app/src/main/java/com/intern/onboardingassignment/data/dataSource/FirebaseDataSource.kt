package com.intern.onboardingassignment.data.dataSource

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface FirebaseDataSource {
    suspend fun logInWithFirebase(email: String, password: String): Flow<Boolean>
    suspend fun signUpWithFirebase(email: String, password: String): Flow<FirebaseUser>
    suspend fun addUserDataToFirebase(name: String, email: String, uId:String)
}