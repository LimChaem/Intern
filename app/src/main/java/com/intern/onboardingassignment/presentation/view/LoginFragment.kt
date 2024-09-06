package com.intern.onboardingassignment.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope

import com.intern.onboardingassignment.MyApp
import com.intern.onboardingassignment.R
import com.intern.onboardingassignment.databinding.FragmentLoginBinding
import com.intern.onboardingassignment.databinding.FragmentSignUpBinding

import kotlinx.coroutines.launch

class LoginFragment : Fragment(), View.OnClickListener {

    private val logInViewModel: LoginViewModel by viewModels {
        val appContainer = (requireActivity().application as MyApp).appContainer
        LoginViewModelFactory(
            sessionManager = appContainer.sessionManager,
        )
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        initView()
        collectFlows()
    }

    private fun collectFlows(){
        viewLifecycleOwner.lifecycleScope.launch {
            logInViewModel.channel.collect{ it ->
                when(it){
                    is LogInEvent.LogInFail -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is LogInEvent.LogInSuccess -> {
                        requireActivity().replaceToFragment(MainPageFragment())
                    }
                }
            }
        }
    }

    private fun initView(){
        binding.tvSignUp.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        p0.let {
            when (it) {
                binding.btnLogin -> {
                    val email = binding.etId.text.toString()
                    val password = binding.etPassword.text.toString()

                    logInViewModel.LogInWithFirebase(email, password)
                }

                binding.tvSignUp -> {
                    requireActivity().replaceToFragment(SignUpFragment())
                }

                else -> Unit
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}