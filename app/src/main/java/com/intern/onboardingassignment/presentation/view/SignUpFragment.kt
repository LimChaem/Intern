package com.intern.onboardingassignment.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.intern.onboardingassignment.MyApp
import com.intern.onboardingassignment.R
import com.intern.onboardingassignment.databinding.FragmentSignUpBinding
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels{
        val appContainer = (requireActivity().application as MyApp).appContainer
        SignUpViewmodelFactory(
            signUpWithFirebaseUseCase = appContainer.signUpWithFirebaseUseCase,
            sessionManager = appContainer.sessionManager,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectFlows()

        binding.btnSignUp.setOnClickListener {
            signUp()
        }

    }
    private fun signUp(){
        val name = binding.etName.text.toString()
        val password = binding.etPassword.text.toString()
        val email = binding.etId.text.toString()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signUp(name = name, password = password, email = email)
        }
    }
    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.channel.collect{
                when(it){
                    is SignUpEvent.SignUpFail -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is SignUpEvent.SignUpSuccess -> {
                        Toast.makeText(requireContext(), "회원 가입 성공!", Toast.LENGTH_SHORT).show()

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.frame, LoginFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}