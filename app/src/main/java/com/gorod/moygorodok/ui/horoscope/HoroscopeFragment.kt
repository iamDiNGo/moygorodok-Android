package com.gorod.moygorodok.ui.horoscope

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gorod.moygorodok.data.local.TokenManager
import com.gorod.moygorodok.data.model.ZodiacSign
import com.gorod.moygorodok.data.remote.model.HoroscopeDataDto
import com.gorod.moygorodok.databinding.FragmentHoroscopeBinding
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Locale

class HoroscopeFragment : Fragment() {

    private var _binding: FragmentHoroscopeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HoroscopeViewModel by viewModels()

    private val signs = ZodiacSign.values().toList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHoroscopeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()
        observeViewModel()

        if (savedInstanceState == null) {
            val initial = initialSign()
            val index = signs.indexOf(initial).coerceAtLeast(0)
            binding.tabsSigns.getTabAt(index)?.select()
            viewModel.selectSign(signs[index])
        }
    }

    private fun initialSign(): ZodiacSign {
        val argSign = arguments?.getString(ARG_SIGN)
        ZodiacSign.fromSlug(argSign)?.let { return it }

        val user = TokenManager.getInstance(requireContext()).user
        ZodiacSign.fromSlug(user?.zodiacSign)?.let { return it }

        return ZodiacSign.ARIES
    }

    private fun setupTabs() {
        signs.forEach { sign ->
            val tab = binding.tabsSigns.newTab().apply {
                text = "${sign.symbol} ${sign.label}"
                tag = sign
            }
            binding.tabsSigns.addTab(tab)
        }

        binding.tabsSigns.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                (tab.tag as? ZodiacSign)?.let(viewModel::selectSign)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun observeViewModel() {
        viewModel.selectedSign.observe(viewLifecycleOwner) { sign ->
            binding.textSymbol.text = sign.symbol
            binding.textSignLabel.text = sign.label
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                HoroscopeUiState.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.cardContent.visibility = View.GONE
                    binding.textEmpty.visibility = View.GONE
                }
                is HoroscopeUiState.Success -> {
                    binding.progress.visibility = View.GONE
                    binding.cardContent.visibility = View.VISIBLE
                    binding.textEmpty.visibility = View.GONE
                    renderHoroscope(state.data)
                }
                HoroscopeUiState.NotAvailable -> {
                    binding.progress.visibility = View.GONE
                    binding.cardContent.visibility = View.GONE
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.textEmpty.text = "На сегодня прогноза нет, загляните позже"
                }
                is HoroscopeUiState.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.cardContent.visibility = View.GONE
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.textEmpty.text = state.message
                }
            }
        }
    }

    private fun renderHoroscope(data: HoroscopeDataDto) {
        binding.textSymbol.text = data.symbol?.takeIf { it.isNotBlank() }
            ?: viewModel.selectedSign.value?.symbol
        binding.textSignLabel.text = data.zodiacSignLabel
            ?: viewModel.selectedSign.value?.label
        binding.textDate.text = formatDate(data.date)
        binding.textBody.text = data.text.orEmpty()
    }

    private fun formatDate(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            val formatter = SimpleDateFormat("d MMMM", Locale("ru", "RU"))
            parser.parse(iso)?.let(formatter::format) ?: iso
        } catch (e: Exception) {
            iso
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_SIGN = "sign"
    }
}
