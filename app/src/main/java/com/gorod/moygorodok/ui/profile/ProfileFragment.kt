package com.gorod.moygorodok.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.repository.AuthRepository
import com.gorod.moygorodok.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private val authRepository = AuthRepository.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!authRepository.isLoggedIn()) {
            findNavController().navigate(R.id.navigation_login)
            return
        }

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.editFirstName.doAfterTextChanged {
            binding.inputLayoutFirstName.error = null
        }

        binding.editLastName.doAfterTextChanged {
            binding.inputLayoutLastName.error = null
        }

        binding.buttonSave.setOnClickListener {
            viewModel.updateProfile(
                firstName = binding.editFirstName.text.toString(),
                lastName = binding.editLastName.text.toString(),
                email = binding.editEmail.text.toString().takeIf { it.isNotBlank() }
            )
        }

        binding.buttonChangeAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_avatar)
        }

        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.navigation_home)
        }

        binding.imageAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_avatar)
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.editFirstName.setText(it.firstName)
                binding.editLastName.setText(it.lastName)
                binding.editEmail.setText(it.email ?: "")
                binding.textPhone.text = it.phone

                // Если есть аватар, можно загрузить его
                // Для демо используем placeholder
                if (it.avatarUrl != null) {
                    // Здесь можно использовать Glide/Coil для загрузки
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setLoading(isLoading)
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Профиль сохранен", Snackbar.LENGTH_SHORT).show()
                viewModel.resetSaveSuccess()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.firstNameError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutFirstName.error = error
        }

        viewModel.lastNameError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutLastName.error = error
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSave.isEnabled = !isLoading
        binding.editFirstName.isEnabled = !isLoading
        binding.editLastName.isEnabled = !isLoading
        binding.editEmail.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
