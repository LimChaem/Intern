package com.intern.onboardingassignment.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.MyApp
import com.intern.onboardingassignment.databinding.ActivityMainBinding
import com.intern.onboardingassignment.domain.session.SessionManager
import com.intern.onboardingassignment.presentation.view.logIn.LoginFragment
import com.intern.onboardingassignment.presentation.view.main.MainPageFragment
import com.intern.onboardingassignment.presentation.extention.addToFragment
import com.intern.onboardingassignment.presentation.extention.replaceToFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<MainViewModel> {
        MyApp().appContainer.mainContainer.mainPageViewModelFactory
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
                viewModel.channel.collect { loginState ->
                    when (loginState) {
                        MainAction.LoggedIn -> {
                            viewModel.userState.collect { userState ->
                                when (userState) {
                                    is LoadUser.Loading -> TODO()
                                    is LoadUser.Success -> {
                                        replaceToFragment(
                                            MainPageFragment(),
                                            clearBackStack = false
                                        )
                                    }

                                    is LoadUser.Error -> {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "사용자 정보를 불러오지 못했습니다. 다시 로그인해주세요.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        viewModel.signOut()
                                    }
                                }
                            }
                        }

                        MainAction.NotLoggedIn -> replaceToFragment(
                            LoginFragment(),
                            clearBackStack = true,
                            addToBackStack = false
                        )
                    }
                }
            }
        }

    }
}