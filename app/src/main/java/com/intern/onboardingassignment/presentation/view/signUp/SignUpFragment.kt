package com.intern.onboardingassignment.presentation.view.signUp

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.intern.onboardingassignment.MyApp
import com.intern.onboardingassignment.R
import com.intern.onboardingassignment.databinding.FragmentSignUpBinding
import com.intern.onboardingassignment.presentation.extention.replaceToFragment
import com.intern.onboardingassignment.presentation.view.logIn.LoginFragment
import kotlinx.coroutines.launch

class SignUpFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels {
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

        initView()
        collectFlows()
        editTextProcess()
        observeLiveData()

    }

    private fun signUp() {
        val name = binding.etName.text.toString()
        val password = binding.etPassword.text.toString()
        val email = binding.etId.text.toString()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signUp(name = name, password = password, email = email)
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.channel.collect {
                when (it) {
                    is SignUpEvent.SignUpFail -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is SignUpEvent.SignUpSuccess -> {
                        Toast.makeText(requireContext(), "회원 가입 성공!", Toast.LENGTH_SHORT).show()

                        requireActivity().replaceToFragment(
                            LoginFragment(),
                            clearBackStack = true,
                            addToBackStack = false
                        )
                    }
                }
            }
        }
    }

    private fun observeLiveData() {
        viewModel.nameValidUi.observe(this, Observer { inValid ->
            binding.tvNameCheck.isVisible = !inValid
        })
        viewModel.emailValidUi.observe(this, Observer { inValid ->
            binding.tvIdCheck.isVisible = !inValid
        })
        viewModel.passwordValidUi.observe(this, Observer { inValid ->
            binding.tvPasswordCheck.isVisible = !inValid
        })
        viewModel.confirmPasswordValidUi.observe(this, Observer { inValid ->
            binding.tvConfirmPasswordCheck.isVisible = !inValid
        })

        viewModel.signUpValid.observe(viewLifecycleOwner, Observer { inValid ->
            binding.btnSignUp.isEnabled = inValid
            if(inValid){
                binding.btnSignUp.setBackgroundResource(R.drawable.btn_radius_lilac)
            }else{
                binding.btnSignUp.setBackgroundResource(R.drawable.btn_radius_false)
            }
        })

    }

    private fun editTextProcess() {
        binding.etName.doAfterTextChanged {
            viewModel.checkName(binding.etName)
        }
        binding.etId.doAfterTextChanged {
            viewModel.checkEmail(binding.etId)
        }
        binding.etPassword.doAfterTextChanged {
            viewModel.checkPassword(binding.etPassword)
        }
        binding.etPasswordCheck.doAfterTextChanged {
            viewModel.checkConfirmPassword(
                binding.etPassword,
                binding.etPasswordCheck
            )
        }
    }

    private fun initView() {
        binding.btnSignUp.setOnClickListener(this)
        binding.tvSignUp.setOnClickListener(this)
    }

    private fun clearText() {
        binding.etName.text.clear()
        binding.etPassword.text?.clear()
        binding.etId.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        v.let {
            when (it) {
                binding.btnSignUp -> signUp()
                binding.tvSignUp -> requireActivity().replaceToFragment(
                    frag = LoginFragment(),
                    clearBackStack = true
                )
            }
        }
    }
}