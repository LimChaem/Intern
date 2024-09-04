package com.intern.onboardingassignment.data.model

import com.google.firebase.Timestamp

data class UserResponse(
    val name: String,
    val email: String,
    val uId: String,
    val createdAt: Timestamp,
)
