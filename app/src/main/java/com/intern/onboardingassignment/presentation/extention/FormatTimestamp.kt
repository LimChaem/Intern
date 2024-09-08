package com.intern.onboardingassignment.presentation.extention

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Timestamp): String {
    val date: Date = timestamp.toDate()

    val sdf = SimpleDateFormat("yyyy년 M월 d일 a hh:mm", Locale.getDefault())

    return sdf.format(date)
}