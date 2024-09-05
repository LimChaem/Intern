package com.intern.onboardingassignment.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.intern.onboardingassignment.MyApp
import com.intern.onboardingassignment.R
import com.intern.onboardingassignment.databinding.FragmentLoginBinding
import com.intern.onboardingassignment.databinding.FragmentMainPageBinding

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.tvSignUp.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onClick(p0: View?) {
        p0.let {
            when (it) {
                binding.btnLogin -> {
                    requireActivity().replaceToFragment(MainPageFragment())
                }

                binding.tvSignUp -> {
                    requireActivity().replaceToFragment(SignUpFragment())
                }

                else -> Unit
            }
        }
    }
}