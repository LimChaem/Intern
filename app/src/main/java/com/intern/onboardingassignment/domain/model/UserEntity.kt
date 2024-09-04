package com.intern.onboardingassignment.domain.model

import com.google.firebase.Timestamp

data class UserEntity(
    val name: String,
    val email: String,
    val uId: String,
    val createdAt: Timestamp,
)

