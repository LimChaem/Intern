package com.intern.onboardingassignment.data.session

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.intern.onboardingassignment.domain.extention.LoginResult
import com.intern.onboardingassignment.domain.session.FirebaseUId
import com.intern.onboardingassignment.domain.session.SessionManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SessionManagerImpl(private val auth: FirebaseAuth) : SessionManager {
    override suspend fun getAuthInformation(): FirebaseUId {
        return auth.uid
    }

    override suspend fun loginWithFirebase(email: String, password: String): Flow<LoginResult> =
        callbackFlow {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    trySend(LoginResult.Success).isSuccess
                } else {
                    trySend(LoginResult.Fail("로그인에 실패하였습니다. 잠시 후 다시 시도해주세요.")).isSuccess
                }
                // TODO 확장 함수로 빼기
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> {
                        trySend(LoginResult.InvalidEmailError).isSuccess
                    }

                    "ERROR_WRONG_PASSWORD" -> {
                        trySend(LoginResult.WrongPasswordError).isSuccess
                    }

                    else -> {
                        trySend(LoginResult.Fail("로그인에 실패하였습니다. 잠시 후 다시 시도해주세요."))
                    }
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> {
                        trySend(LoginResult.UserNotFoundError).isSuccess
                    }

                    "ERROR_USER_DISABLED" -> {
                        trySend(LoginResult.UserDisabledError).isSuccess
                    }

                    else -> {
                        trySend(LoginResult.Fail("로그인에 실패하였습니다. 잠시 후 다시 시도해주세요."))
                    }
                }
            }
            awaitClose { }
        }


    override suspend fun logOut() {
        auth.signOut()
    }

}