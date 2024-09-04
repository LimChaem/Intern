package com.intern.onboardingassignment.presentation.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.intern.onboardingassignment.R

fun FragmentActivity.navigationToFragment(frag: Fragment) {
    supportFragmentManager.beginTransaction()
        .add(R.id.frame, frag)
        .commit()
}