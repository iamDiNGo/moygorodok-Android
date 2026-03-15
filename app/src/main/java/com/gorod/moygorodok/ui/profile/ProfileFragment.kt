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
import com.gorod.moygorodok.data.model.ProfileState
import com.gorod.moygorodok.data.model.User
import com.gorod.moygorodok.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private var editGender: String? = null

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

        if (!viewModel.isLoggedIn()) {
            findNavController().navigate(R.id.navigation_login)
            return
        }

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.editName.doAfterTextChanged {
            binding.inputLayoutName.error = null
        }

        binding.buttonEdit.setOnClickListener {
            viewModel.toggleEditMode()
        }

        binding.buttonCancel.setOnClickListener {
            viewModel.cancelEdit()
            viewModel.user.value?.let { populateFields(it) }
        }

        binding.buttonSave.setOnClickListener {
            viewModel.saveProfile(
                name = binding.editName.text.toString(),
                email = binding.editEmail.text.toString().takeIf { it.isNotBlank() },
                gender = editGender
            )
        }

        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.buttonLogoutAll.setOnClickListener {
            viewModel.logoutAll()
        }

        binding.imageAvatar.setOnClickListener {
            if (viewModel.isEditing.value == true) {
                findNavController().navigate(R.id.action_profile_to_avatar)
            }
        }

        binding.genderGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                editGender = when (checkedId) {
                    R.id.button_male -> "male"
                    R.id.button_female -> "female"
                    else -> null
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let { populateFields(it) }
        }

        viewModel.profileState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ProfileState.Idle -> setLoading(false)
                is ProfileState.Loading -> setLoading(true)
                is ProfileState.Success -> setLoading(false)
                is ProfileState.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Профиль обновлен", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.isEditing.observe(viewLifecycleOwner) { isEditing ->
            setEditMode(isEditing)
        }

        viewModel.nameError.observe(viewLifecycleOwner) { error ->
            binding.inputLayoutName.error = error
        }

        viewModel.loggedOut.observe(viewLifecycleOwner) { loggedOut ->
            if (loggedOut) {
                findNavController().navigate(R.id.navigation_home)
            }
        }
    }

    private fun populateFields(user: User) {
        binding.editName.setText(user.name)
        binding.editEmail.setText(user.email ?: "")
        binding.textPhone.text = user.phone

        editGender = user.gender
        when (user.gender) {
            "male" -> binding.genderGroup.check(R.id.button_male)
            "female" -> binding.genderGroup.check(R.id.button_female)
        }

        user.createdAt?.let { date ->
            binding.textCreatedAt.text = getString(R.string.registered_at, formatDate(date))
            binding.textCreatedAt.visibility = View.VISIBLE
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val parts = dateString.split("T")
            parts[0]
        } catch (e: Exception) {
            dateString
        }
    }

    private fun setEditMode(isEditing: Boolean) {
        binding.editName.isEnabled = isEditing
        binding.editEmail.isEnabled = isEditing
        binding.genderGroup.isEnabled = isEditing
        binding.buttonMale.isEnabled = isEditing
        binding.buttonFemale.isEnabled = isEditing

        binding.buttonEdit.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.buttonSave.visibility = if (isEditing) View.VISIBLE else View.GONE
        binding.buttonCancel.visibility = if (isEditing) View.VISIBLE else View.GONE
        binding.buttonLogout.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.buttonLogoutAll.visibility = if (isEditing) View.GONE else View.VISIBLE
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSave.isEnabled = !isLoading
        binding.buttonEdit.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
