package com.intern.onboardingassignment.data.dataSource

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.intern.onboardingassignment.data.model.UserResponse
import com.intern.onboardingassignment.domain.extention.SuchEmailResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    override suspend fun checkDuplicateEmail(email: String): Flow<SuchEmailResult> = flow {
        try {
            val result = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (result.isEmpty) {
                emit(SuchEmailResult.Empty)
            } else {
                emit(SuchEmailResult.Exits)
            }
        } catch (e: Exception) {
            emit(SuchEmailResult.Error(e))
        }
    }

    override suspend fun getCurrentUserData(): Flow<UserResponse> = flow {
        val uId = auth.currentUser?.uid

        Log.e("checkLoginStatus1", "Error: ${uId}")

        if (uId != null) {
            val documentSnapshot = db.collection("users").document(uId).get().await()
            val userResponse = documentSnapshot.toObject(UserResponse::class.java)

            if (userResponse != null) {
                Log.d("getCurrentUser", "UserEntity: ${userResponse}")
                emit(userResponse)
            } else {
                return@flow
            }
        } else {
            return@flow
        }
    }.catch { e ->
        throw e
    }

    override suspend fun accountDeletionWithFirebase(): Flow<Boolean> = flow {
        val user = auth.currentUser
        val uId = user?.uid
        if (uId != null) {
            val documentRef = db.collection("users").document(uId)

            documentRef.delete().await()
            user.delete().await()
            emit(true)
        }else{
            emit(false)
        }
    }
}
