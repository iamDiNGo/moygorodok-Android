package com.gorod.moygorodok.ui.auth.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.VerifyCodeState
import com.gorod.moygorodok.databinding.FragmentPinBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class PinFragment : Fragment() {

    private var _binding: FragmentPinBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PinViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phone = arguments?.getString("phone") ?: ""
        val userExists = arguments?.getBoolean("user_exists", true) ?: true
        val retryAfter = arguments?.getInt("retry_after", 60) ?: 60

        if (viewModel.phone.isEmpty()) {
            viewModel.init(phone, userExists, retryAfter)
        }

        binding.textSubtitle.text = getString(R.string.code_sent_to, formatPhoneDisplay(phone))

        setupNumpad()
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

    private fun setupNumpad() {
        val digitButtons = listOf(
            binding.button0, binding.button1, binding.button2,
            binding.button3, binding.button4, binding.button5,
            binding.button6, binding.button7, binding.button8,
            binding.button9
        )

        digitButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.addDigit(index.toString())
            }
        }

        binding.buttonDelete.setOnClickListener {
            viewModel.removeDigit()
        }

        binding.buttonResend.setOnClickListener {
            viewModel.resendCode()
        }

        binding.buttonChangeNumber.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.pinDigits.observe(viewLifecycleOwner) { pin ->
            updatePinIndicators(pin.length)

            if (pin.length == 4) {
                viewModel.verifyCode()
            }
        }

        viewModel.verifyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is VerifyCodeState.Idle -> {
                    setLoading(false)
                }
                is VerifyCodeState.Loading -> {
                    setLoading(true)
                }
                is VerifyCodeState.Success -> {
                    setLoading(false)
                    findNavController().navigate(R.id.navigation_profile)
                    viewModel.resetState()
                }
                is VerifyCodeState.NeedRegistration -> {
                    setLoading(false)
                    val bundle = Bundle().apply {
                        putString("phone", state.phone)
                        putString("code", viewModel.code)
                    }
                    findNavController().navigate(R.id.action_pin_to_register, bundle)
                    viewModel.resetState()
                }
                is VerifyCodeState.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                is VerifyCodeState.Blocked -> {
                    setLoading(false)
                    showBlockedDialog(state.reason)
                    viewModel.resetState()
                }
            }
        }

        viewModel.timerSeconds.observe(viewLifecycleOwner) { seconds ->
            if (seconds > 0) {
                binding.textTimer.visibility = View.VISIBLE
                binding.textTimer.text = getString(R.string.resend_timer, formatTimer(seconds))
            } else {
                binding.textTimer.visibility = View.GONE
            }
        }

        viewModel.canResend.observe(viewLifecycleOwner) { canResend ->
            binding.buttonResend.visibility = if (canResend) View.VISIBLE else View.GONE
        }
    }

    private fun formatTimer(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return "$min:${sec.toString().padStart(2, '0')}"
    }

    private fun showBlockedDialog(reason: String?) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.account_blocked_title)
            .setMessage(
                if (reason != null) getString(R.string.account_blocked_message, reason)
                else getString(R.string.account_blocked_no_reason)
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                findNavController().navigateUp()
            }
            .setCancelable(false)
            .show()
    }

    private fun updatePinIndicators(filledCount: Int) {
        val indicators = listOf(
            binding.pinIndicator1,
            binding.pinIndicator2,
            binding.pinIndicator3,
            binding.pinIndicator4
        )

        indicators.forEachIndexed { index, indicator ->
            indicator.isActivated = index < filledCount
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        val numpadButtons = listOf(
            binding.button0, binding.button1, binding.button2,
            binding.button3, binding.button4, binding.button5,
            binding.button6, binding.button7, binding.button8,
            binding.button9, binding.buttonDelete
        )

        numpadButtons.forEach { it.isEnabled = !isLoading }
        binding.buttonResend.isEnabled = !isLoading
        binding.buttonChangeNumber.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
