package com.intern.onboardingassignment.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.intern.onboardingassignment.data.dataSource.FirebaseDataSource
import com.intern.onboardingassignment.data.mapper.toUserEntity
import com.intern.onboardingassignment.domain.repository.AuthRepository
import com.intern.onboardingassignment.domain.extention.SignUpResult
import com.intern.onboardingassignment.domain.extention.SuchEmailResult
import com.intern.onboardingassignment.domain.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(private val firebaseDataSource: FirebaseDataSource) : AuthRepository {

    override suspend fun signUp(name: String, email: String, password: String): Flow<SignUpResult> =
        flow {
            firebaseDataSource.signUpWithFirebase(email = email, password = password)
                .catch { e ->
                    when (e) {
                        is FirebaseAuthWeakPasswordException -> emit(SignUpResult.WeakPasswordError)
                        is FirebaseAuthInvalidCredentialsException -> emit(SignUpResult.InvalidEmailError)
                        is FirebaseAuthUserCollisionException -> emit(SignUpResult.UserCollisionError)
                        is FirebaseAuthException -> emit(SignUpResult.Failure("FirebaseAuthException: ${e.message}"))
                        else -> emit(SignUpResult.Failure("Unexpected error: ${e.localizedMessage}"))
                    }
                }

                .collect { user ->
                    try {
                        firebaseDataSource.addUserDataToFirebase(
                            name = name,
                            email = email,
                            uId = user.uid,
                        )

                        emit(value = SignUpResult.Success)
                    } catch (e: Exception) {
                        emit(SignUpResult.Failure("Failed to add user data: ${e.localizedMessage}"))
                    }
                }
        }

    override suspend fun checkEmailDuplicate(email: String): Flow<SuchEmailResult> {
        return firebaseDataSource.checkDuplicateEmail(email = email)
            .catch { e ->
                SuchEmailResult.Error(e)
            }

    }

    override suspend fun getCurrentUserData(): Flow<UserEntity> = flow {
        firebaseDataSource.getCurrentUserData()
            .catch { e ->
                throw e
            }
            .collect {
                val userModel = it.toUserEntity()
                Log.d("getCurrentUser", "UserEntity: $userModel")
                emit(userModel)
            }
    }

    override suspend fun accountDeletion(): Flow<Boolean> = flow {
        firebaseDataSource.accountDeletionWithFirebase()
            .catch { e ->
                emit(false)
            }.collect{result ->
                emit(result)
        }
    }
}
