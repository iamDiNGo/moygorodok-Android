package com.gorod.moygorodok.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.AuthState
import com.gorod.moygorodok.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.editPhone.doAfterTextChanged {
            binding.inputLayoutPhone.error = null
        }

        binding.buttonLogin.setOnClickListener {
            val phone = binding.editPhone.text.toString()
            viewModel.login(phone)
        }

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Idle -> {
                    setLoading(false)
                }
                is AuthState.Loading -> {
                    setLoading(true)
                }
                is AuthState.Success -> {
                    setLoading(false)
                    findNavController().navigate(R.id.action_login_to_pin)
                    viewModel.resetState()
                }
                is AuthState.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }
            }
        }

        viewModel.phoneError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutPhone.error = error
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !isLoading
        binding.buttonRegister.isEnabled = !isLoading
        binding.editPhone.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
