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

    private var selectedGender: String? = null

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

        val phone = arguments?.getString("phone") ?: ""
        val code = arguments?.getString("code") ?: ""

        if (viewModel.phone.isEmpty()) {
            viewModel.init(phone, code)
        }

        binding.textPhone.text = formatPhoneDisplay(phone)

        setupViews()
        observeViewModel()
    }

    private fun formatPhoneDisplay(phone: String): String {
        val digits = phone.replace("[^0-9]".toRegex(), "")
        return if (digits.length == 11) {
            "+${digits[0]} (${digits.substring(1, 4)}) ${digits.substring(4, 7)}-${digits.substring(7, 9)}-${digits.substring(9, 11)}"
        } else {
            phone
        }
    }

    private fun setupViews() {
        binding.editName.doAfterTextChanged {
            binding.inputLayoutName.error = null
        }

        binding.genderGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedGender = when (checkedId) {
                    R.id.button_male -> "male"
                    R.id.button_female -> "female"
                    else -> null
                }
                binding.textGenderError.visibility = View.GONE
            }
        }

        binding.buttonRegister.setOnClickListener {
            viewModel.register(
                name = binding.editName.text.toString(),
                gender = selectedGender
            )
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
                    findNavController().navigate(R.id.navigation_profile)
                    viewModel.resetState()
                }
                is RegisterState.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                is RegisterState.CodeExpired -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    findNavController().popBackStack(R.id.navigation_login, false)
                    viewModel.resetState()
                }
            }
        }

        viewModel.nameError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutName.error = error
        }

        viewModel.genderError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.textGenderError.text = error
                binding.textGenderError.visibility = View.VISIBLE
            } else {
                binding.textGenderError.visibility = View.GONE
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonRegister.isEnabled = !isLoading
        binding.editName.isEnabled = !isLoading
        binding.genderGroup.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
