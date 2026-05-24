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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private var editGender: String? = null
    private var editBirthday: String? = null

    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val displayFormatter = SimpleDateFormat("d MMMM yyyy", Locale("ru", "RU")).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

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
                gender = editGender,
                birthday = editBirthday
            )
        }

        binding.buttonMyReports.setOnClickListener {
            findNavController().navigate(R.id.navigation_my_reports)
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

        binding.editBirthday.setOnClickListener {
            if (viewModel.isEditing.value == true) showDatePicker()
        }
        binding.inputLayoutBirthday.setEndIconOnClickListener {
            if (viewModel.isEditing.value == true) showDatePicker()
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

    private fun showDatePicker() {
        val initial = editBirthday?.let {
            runCatching { isoFormatter.parse(it)?.time }.getOrNull()
        } ?: MaterialDatePicker.todayInUtcMilliseconds()

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Дата рождения")
            .setSelection(initial)
            .setCalendarConstraints(constraints)
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val iso = isoFormatter.format(Date(selection))
            editBirthday = iso
            binding.editBirthday.setText(displayFormatter.format(Date(selection)))
        }
        picker.show(parentFragmentManager, "birthday_picker")
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

        editBirthday = user.birthday
        binding.editBirthday.setText(formatBirthday(user.birthday))

        val zodiac = user.zodiacSignLabel?.takeIf { it.isNotBlank() }
        if (zodiac != null) {
            binding.textZodiac.text = "${zodiacSymbol(user.zodiacSign)} $zodiac"
            binding.textZodiac.visibility = View.VISIBLE
        } else {
            binding.textZodiac.visibility = View.GONE
        }

        user.createdAt?.let { date ->
            binding.textCreatedAt.text = getString(R.string.registered_at, formatDate(date))
            binding.textCreatedAt.visibility = View.VISIBLE
        }
    }

    private fun formatBirthday(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        return try {
            isoFormatter.parse(iso)?.let(displayFormatter::format) ?: iso
        } catch (e: Exception) {
            iso
        }
    }

    private fun zodiacSymbol(sign: String?): String = when (sign) {
        "aries" -> "♈"
        "taurus" -> "♉"
        "gemini" -> "♊"
        "cancer" -> "♋"
        "leo" -> "♌"
        "virgo" -> "♍"
        "libra" -> "♎"
        "scorpio" -> "♏"
        "sagittarius" -> "♐"
        "capricorn" -> "♑"
        "aquarius" -> "♒"
        "pisces" -> "♓"
        else -> "✨"
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
        binding.inputLayoutBirthday.isEnabled = isEditing
        binding.editBirthday.isEnabled = isEditing

        binding.buttonEdit.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.buttonSave.visibility = if (isEditing) View.VISIBLE else View.GONE
        binding.buttonCancel.visibility = if (isEditing) View.VISIBLE else View.GONE
        binding.buttonMyReports.visibility = if (isEditing) View.GONE else View.VISIBLE
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
