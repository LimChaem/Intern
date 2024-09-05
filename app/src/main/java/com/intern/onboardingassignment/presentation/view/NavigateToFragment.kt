package com.intern.onboardingassignment.presentation.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.intern.onboardingassignment.R

fun FragmentActivity.addToFragment(frag: Fragment) {
    supportFragmentManager.beginTransaction()
        .add(R.id.frame, frag)
        .commit()
}

fun FragmentActivity.replaceToFragment(frag:Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(R.id.frame, frag)
        .addToBackStack(null)
        .commit()
}