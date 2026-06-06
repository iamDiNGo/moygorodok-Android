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
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HoroscopeFragment : Fragment() {

    private var _binding: FragmentHoroscopeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HoroscopeViewModel by viewModels()

    private val signs = ZodiacSign.values().toList()
    private val periods = HoroscopePeriod.values().toList()

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

        setupSignTabs()
        setupPeriodTabs()
        observeViewModel()

        binding.buttonRetry.setOnClickListener { viewModel.retry() }

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

    private fun setupSignTabs() {
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
            override fun onTabReselected(tab: TabLayout.Tab) {
                viewModel.retry()
            }
        })
    }

    private fun setupPeriodTabs() {
        periods.forEach { period ->
            val tab = binding.tabsPeriods.newTab().apply {
                text = period.label
                tag = period
            }
            binding.tabsPeriods.addTab(tab)
        }

        binding.tabsPeriods.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                (tab.tag as? HoroscopePeriod)?.let(viewModel::selectPeriod)
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

        // Контент зависит и от статуса загрузки бандла, и от выбранного периода —
        // оба наблюдателя ведут к единому renderContent().
        viewModel.state.observe(viewLifecycleOwner) { renderContent() }
        viewModel.selectedPeriod.observe(viewLifecycleOwner) { renderContent() }
    }

    private fun renderContent() {
        val state = viewModel.state.value ?: return
        val period = viewModel.selectedPeriod.value ?: HoroscopePeriod.TODAY

        when (state) {
            HoroscopeUiState.Loading -> {
                binding.progress.visibility = View.VISIBLE
                binding.cardContent.visibility = View.GONE
                binding.layoutMessage.visibility = View.GONE
            }
            is HoroscopeUiState.Error -> {
                binding.progress.visibility = View.GONE
                binding.cardContent.visibility = View.GONE
                binding.layoutMessage.visibility = View.VISIBLE
                binding.textEmpty.text = state.message
                binding.buttonRetry.visibility = View.VISIBLE
            }
            is HoroscopeUiState.Success -> {
                val data = state.bundle.periodData(period)
                binding.progress.visibility = View.GONE
                if (data != null) {
                    binding.cardContent.visibility = View.VISIBLE
                    binding.layoutMessage.visibility = View.GONE
                    renderHoroscope(data, period)
                } else {
                    // Нет прогноза на конкретный период — пустое состояние, не ошибка.
                    binding.cardContent.visibility = View.GONE
                    binding.layoutMessage.visibility = View.VISIBLE
                    binding.textEmpty.text = "На этот период прогноза нет, загляните позже"
                    binding.buttonRetry.visibility = View.GONE
                }
            }
        }
    }

    private fun renderHoroscope(data: HoroscopeDataDto, period: HoroscopePeriod) {
        binding.textDate.text = displayDate(data.date, period)
        binding.textBody.text = data.text.orEmpty()
    }

    /**
     * Дата для отображения. Бэк отдаёт только дату НАЧАЛА периода (weekly — понедельник,
     * monthly — 1-е число), поэтому конец периода и диапазон вычисляются на клиенте:
     *  - today/tomorrow → одна дата: «31 мая 2026»
     *  - weekly         → понедельник…+6 дней: «25–31 мая 2026»
     *  - monthly        → 1-е…последний день месяца: «1–31 мая 2026»
     */
    private fun displayDate(iso: String?, period: HoroscopePeriod): String {
        if (iso.isNullOrBlank()) return ""
        val start = parseIso(iso) ?: return iso

        return when (period) {
            HoroscopePeriod.TODAY, HoroscopePeriod.TOMORROW -> FULL.format(start)
            HoroscopePeriod.WEEKLY -> {
                val end = Calendar.getInstance().apply {
                    time = start
                    add(Calendar.DAY_OF_MONTH, 6)
                }.time
                formatRange(start, end)
            }
            HoroscopePeriod.MONTHLY -> {
                val end = Calendar.getInstance().apply {
                    time = start
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                }.time
                formatRange(start, end)
            }
        }
    }

    /** Сворачивает общие компоненты диапазона (как iOS DateIntervalFormatter, locale ru). */
    private fun formatRange(start: Date, end: Date): String {
        val calStart = Calendar.getInstance().apply { time = start }
        val calEnd = Calendar.getInstance().apply { time = end }
        val sameYear = calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR)
        val sameMonth = sameYear && calStart.get(Calendar.MONTH) == calEnd.get(Calendar.MONTH)

        return when {
            sameMonth -> "${DAY.format(start)}–${FULL.format(end)}"          // 25–31 мая 2026
            sameYear -> "${DAY_MONTH.format(start)} – ${FULL.format(end)}"    // 28 мая – 3 июня 2026
            else -> "${FULL.format(start)} – ${FULL.format(end)}"            // 29 декабря 2025 – 4 января 2026
        }
    }

    private fun parseIso(iso: String): Date? = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(iso)
    } catch (e: Exception) {
        null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_SIGN = "sign"

        private val RU = Locale("ru", "RU")
        private val DAY = SimpleDateFormat("d", RU)
        private val DAY_MONTH = SimpleDateFormat("d MMMM", RU)
        private val FULL = SimpleDateFormat("d MMMM yyyy", RU)
    }
}
