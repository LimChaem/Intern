package com.intern.onboardingassignment.data.dataSource

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.intern.onboardingassignment.data.model.UserResponse
import com.intern.onboardingassignment.domain.utill.SignUpResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseDataSourceImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : FirebaseDataSource {
    override suspend fun signUpWithFirebase(
        email: String,
        password: String
    ): Flow<FirebaseUser> = flow {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                emit(value = user)
            } else {
                throw FirebaseAuthException("USER_CREATION_FAILED", "Failed to create user")
            }
        } catch (e: FirebaseAuthException) {
            throw e // 예외를 그대로 던지거나, 적절한 SignUpResult를 방출
        }
    }

    override suspend fun addUserDataToFirebase(name: String, email: String, uId: String) {
        val userData =
            UserResponse(name = name, email = email, uId = uId, createdAt = Timestamp.now())
        db.collection("users").document(uId).set(userData).await()
    }
}