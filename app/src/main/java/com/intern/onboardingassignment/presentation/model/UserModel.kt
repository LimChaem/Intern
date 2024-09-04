package com.intern.onboardingassignment.presentation.model

import com.google.firebase.Timestamp

data class UserModel(
    val name: String,
    val email: String,
    val uId: String,
    val createdAt: Timestamp,
)
