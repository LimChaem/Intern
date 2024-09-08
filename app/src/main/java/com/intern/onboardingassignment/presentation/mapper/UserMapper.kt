package com.intern.onboardingassignment.presentation.mapper

import com.intern.onboardingassignment.domain.model.UserEntity
import com.intern.onboardingassignment.presentation.model.UserModel

fun UserEntity.toUserModel() = UserModel(
    name = name, email = email, uId = uId, createdAt = createdAt
)

fun UserModel.toUserEntity() = UserEntity(
    name = name, email = email, uId = uId, createdAt = createdAt
)