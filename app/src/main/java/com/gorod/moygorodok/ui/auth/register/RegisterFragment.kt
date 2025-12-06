package com.gorod.moygorodok.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.RegisterState
import com.gorod.moygorodok.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
        binding.editFirstName.doAfterTextChanged {
            binding.inputLayoutFirstName.error = null
        }
        binding.editLastName.doAfterTextChanged {
            binding.inputLayoutLastName.error = null
        }
        binding.editPin.doAfterTextChanged {
            binding.inputLayoutPin.error = null
        }
        binding.editPinConfirm.doAfterTextChanged {
            binding.inputLayoutPinConfirm.error = null
        }

        binding.buttonRegister.setOnClickListener {
            viewModel.register(
                phone = binding.editPhone.text.toString(),
                firstName = binding.editFirstName.text.toString(),
                lastName = binding.editLastName.text.toString(),
                email = binding.editEmail.text.toString().takeIf { it.isNotBlank() },
                pin = binding.editPin.text.toString(),
                pinConfirm = binding.editPinConfirm.text.toString()
            )
        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterState.Idle -> {
                    setLoading(false)
                }
                is RegisterState.Loading -> {
                    setLoading(true)
                }
                is RegisterState.Success -> {
                    setLoading(false)
                    findNavController().navigate(R.id.action_register_to_avatar)
                    viewModel.resetState()
                }
                is RegisterState.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }
            }
        }

        viewModel.phoneError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutPhone.error = error
        }

        viewModel.firstNameError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutFirstName.error = error
        }

        viewModel.lastNameError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutLastName.error = error
        }

        viewModel.pinError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutPin.error = error
        }

        viewModel.pinConfirmError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutPinConfirm.error = error
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonRegister.isEnabled = !isLoading
        binding.buttonBack.isEnabled = !isLoading
        binding.editPhone.isEnabled = !isLoading
        binding.editFirstName.isEnabled = !isLoading
        binding.editLastName.isEnabled = !isLoading
        binding.editEmail.isEnabled = !isLoading
        binding.editPin.isEnabled = !isLoading
        binding.editPinConfirm.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
