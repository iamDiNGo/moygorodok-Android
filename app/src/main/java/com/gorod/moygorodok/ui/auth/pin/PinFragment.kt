package com.gorod.moygorodok.ui.auth.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gorod.moygorodok.AuthActivity
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.PinState
import com.gorod.moygorodok.databinding.FragmentPinBinding
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

        setupNumpad()
        observeViewModel()
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
    }

    private fun observeViewModel() {
        viewModel.pinDigits.observe(viewLifecycleOwner) { pin ->
            updatePinIndicators(pin.length)

            if (pin.length == 4) {
                viewModel.verifyPin()
            }
        }

        viewModel.pinState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PinState.Idle -> {
                    setLoading(false)
                }
                is PinState.Loading -> {
                    setLoading(true)
                }
                is PinState.Success -> {
                    setLoading(false)
                    (activity as? AuthActivity)?.navigateToMain()
                    viewModel.resetState()
                }
                is PinState.Error -> {
                    setLoading(false)
                    val message = if (state.attemptsLeft > 0) {
                        "${state.message}. Осталось попыток: ${state.attemptsLeft}"
                    } else {
                        state.message
                    }
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }
            }
        }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
