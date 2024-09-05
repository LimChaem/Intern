package com.intern.onboardingassignment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.intern.onboardingassignment.data.dataSource.FirebaseDataSourceImpl
import com.intern.onboardingassignment.data.repository.AuthRepositoryImpl
import com.intern.onboardingassignment.data.session.SessionManagerImpl
import com.intern.onboardingassignment.domain.session.SessionManager
import com.intern.onboardingassignment.domain.usecase.SignUpWithFirebaseUseCase
import com.intern.onboardingassignment.presentation.view.LoginViewModelFactory
import com.intern.onboardingassignment.presentation.view.MainPageViewModelFactory
import com.intern.onboardingassignment.presentation.view.SignUpViewmodelFactory

class AppContainer {

    val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val firebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val firebaseDataRepository by lazy {
        FirebaseDataSourceImpl(auth = firebaseAuth, db = firebaseFirestore)
    }

    private val authRepository by lazy {
        AuthRepositoryImpl(firebaseDataSource = firebaseDataRepository)
    }

    val sessionManager by lazy {
        SessionManagerImpl(auth = firebaseAuth)
    }

    val signUpWithFirebaseUseCase by lazy {
        SignUpWithFirebaseUseCase(authRepository = authRepository)
    }

    // container 초기화
    val signUpContainer: SignUpContainer by lazy {
        SignUpContainer(
            signUpWithFirebaseUseCase = signUpWithFirebaseUseCase,
            sessionManager = sessionManager,
        )
    }

    val logInContainer: LogInContainer by lazy {
        LogInContainer(sessionManager = sessionManager)
    }

    val mainContainer: MainContainer by lazy {
        MainContainer(sessionManager = sessionManager)
    }


    // container
    class SignUpContainer(
        val signUpWithFirebaseUseCase: SignUpWithFirebaseUseCase,
        val sessionManager: SessionManager,
    ) {
        val signUpFactory = SignUpViewmodelFactory(
            signUpWithFirebaseUseCase = signUpWithFirebaseUseCase,
            sessionManager = sessionManager,
        )
    }

    class LogInContainer(
        val sessionManager: SessionManager,
    ) {
        val loginViewModelFactory = LoginViewModelFactory(
            sessionManager = sessionManager,
        )
    }

    class MainContainer(
        val sessionManager: SessionManager,
    ) {
        val mainPageViewModelFactory = MainViewModelFactory(
            sessionManager = sessionManager,
        )
    }

}