package com.intern.onboardingassignment.presentation.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.intern.onboardingassignment.MyApp
import com.intern.onboardingassignment.R
import com.intern.onboardingassignment.databinding.FragmentMainPageBinding
import com.intern.onboardingassignment.databinding.FragmentSignUpBinding
import com.intern.onboardingassignment.presentation.CurrentUser
import com.intern.onboardingassignment.presentation.extention.formatTimestamp
import com.intern.onboardingassignment.presentation.extention.replaceToFragment
import com.intern.onboardingassignment.presentation.view.logIn.LoginFragment
import com.intern.onboardingassignment.presentation.view.signUp.SignUpViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainPageFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainPageViewModel by viewModels {
        MyApp().appContainer.mainPageContainer.mainPageViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectFlows()
        initView()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.loadingState.collect {
                    when (it) {
                        is LoadingState.Loading -> {
                            viewModel.getCurrentUser()
                            binding.constMain.visibility = View.GONE
                            binding.pbMainLoading.visibility = View.VISIBLE
                        }

                        is LoadingState.Success -> {
                            setUserData()
                            binding.pbMainLoading.visibility = View.GONE
                            binding.constMain.visibility = View.VISIBLE
                        }

                        is LoadingState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "정보 불러오기가 실패 하였습니다. 다시 시도 해 주세요.",
                                Toast.LENGTH_SHORT
                            ).show()

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
    }

    private fun setUserData() {
        val user = CurrentUser.userData
        if (user != null) {

            val firebaseTimestamp = user.createdAt
            val formattedDate = formatTimestamp(firebaseTimestamp)

            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.tvCreatedAt.text = formattedDate
        }
    }

    private fun initView() {
        binding.btnLogOut.setOnClickListener(this)
        binding.btnAccountDeletion.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onClick(v: View?) {
        v.let {
            when (it) {
                binding.btnLogOut -> {

                    viewModel.logOut()
                    requireActivity().replaceToFragment(
                        LoginFragment(),
                        clearBackStack = true,
                        addToBackStack = false
                    )
                }

                binding.btnAccountDeletion -> {

                    viewModel.accountDeletion()
                    requireActivity().replaceToFragment(
                        LoginFragment(),
                        clearBackStack = true,
                        addToBackStack = false
                    )
                }

                else -> {}
            }

        }
    }
}