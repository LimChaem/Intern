package com.intern.onboardingassignment.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.MyApp
import com.intern.onboardingassignment.databinding.ActivityMainBinding
import com.intern.onboardingassignment.presentation.view.logIn.LoginFragment
import com.intern.onboardingassignment.presentation.view.main.MainPageFragment
import com.intern.onboardingassignment.presentation.extention.addToFragment
import com.intern.onboardingassignment.presentation.extention.replaceToFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<MainViewModel> {
        val appContainer =
            (application as MyApp).appContainer
        MainViewModelFactory(
            sessionManager = appContainer.sessionManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navigateToScreen()
    }


    private fun navigateToScreen() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.viewModelScope.launch {
                    viewModel.channel.collect { loginState ->
                        when (loginState) {
                            MainAction.LoggedIn -> replaceToFragment(MainPageFragment(),false)
                            MainAction.NotLoggedIn -> replaceToFragment(LoginFragment(), true)
                        }
                    }
                }
            }
        }
    }
}