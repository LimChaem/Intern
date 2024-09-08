package com.intern.onboardingassignment.domain.extention

sealed class SuchEmailResult {
    object Exits:SuchEmailResult()
    object Empty: SuchEmailResult()
    data class Error(val e: Throwable): SuchEmailResult()
}