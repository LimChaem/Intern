package com.intern.onboardingassignment.data.mapper

import com.intern.onboardingassignment.data.model.UserResponse
import com.intern.onboardingassignment.domain.model.UserEntity

fun UserResponse.toUserEntity() = UserEntity(
    name = name, email = email, uId = uId, createdAt = createdAt
)

fun UserEntity.toUserResponse() = UserResponse(
    name = name, email = email, uId = uId, createdAt = createdAt
)