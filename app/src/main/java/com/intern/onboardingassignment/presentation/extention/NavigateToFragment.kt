package com.intern.onboardingassignment.presentation.extention

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import com.intern.onboardingassignment.R

fun FragmentActivity.addToFragment(frag: Fragment) {
    supportFragmentManager.beginTransaction()
        .add(R.id.frame, frag)
        .commit()
}

fun FragmentActivity.replaceToFragment(frag: Fragment, clearBackStack: Boolean = false, addToBackStack: Boolean = false) {

    val fragmentManager = supportFragmentManager
    val fragmentTag = frag::class.java.simpleName
    val fragmentTransaction=fragmentManager.beginTransaction().replace(R.id.frame, frag, frag::class.java.simpleName)

    val isFragmentInBackStack = supportFragmentManager.findFragmentByTag(fragmentTag) != null

    if(clearBackStack){
        supportFragmentManager.popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    if(addToBackStack && !isFragmentInBackStack){
        fragmentTransaction.addToBackStack(frag::class.java.simpleName).commit()
    } else {
        fragmentTransaction.commit()
    }
}
