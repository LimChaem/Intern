package com.intern.onboardingassignment.presentation.extention

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import com.intern.onboardingassignment.R

fun FragmentActivity.addToFragment(frag: Fragment) {
    supportFragmentManager.beginTransaction()
        .add(R.id.frame, frag)
        .commit()
}

fun FragmentActivity.replaceToFragment(frag: Fragment, clearBackStack: Boolean = false, addToBackStack: Boolean = false) {

    val fragmentManager = supportFragmentManager.beginTransaction().replace(R.id.frame, frag)

    if(addToBackStack){
        fragmentManager.addToBackStack(null).commit()
    } else {

        fragmentManager.commit()
    }
}
