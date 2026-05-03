package com.gorod.moygorodok.ui.city

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.data.location.LocationProvider
import com.gorod.moygorodok.databinding.FragmentCitySelectionBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CitySelectionBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "CitySelectionBottomSheet"
        const val RESULT_KEY = "city_selected"
        const val ARG_CITY_ID = "city_id"
        const val ARG_CITY_NAME = "city_name"
        const val ARG_CITY_REGION = "city_region"
    }

    private var _binding: FragmentCitySelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CitySelectionViewModel by viewModels()
    private lateinit var adapter: CityAdapter

    private val locationProvider by lazy { LocationProvider.getInstance(requireContext()) }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.any { it }
        if (granted) tryLocate()
        else Snackbar.make(binding.root, "Без разрешения нельзя определить город", Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val sheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            sheet?.let {
                BottomSheetBehavior.from(it).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                }
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CityAdapter { city -> viewModel.selectCity(city) }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        binding.buttonClose.setOnClickListener { dismiss() }

        binding.editSearch.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString().orEmpty()
            binding.buttonClear.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
            viewModel.onQueryChanged(query)
        }

        binding.buttonClear.setOnClickListener {
            binding.editSearch.setText("")
        }

        binding.buttonLocate.setOnClickListener {
            if (locationProvider.hasPermission()) tryLocate()
            else permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        observeState()
        observeSelection()
    }

    private fun tryLocate() {
        viewLifecycleOwner.lifecycleScope.launch {
            locationProvider.getCurrent()
                .onSuccess { loc ->
                    viewModel.loadNearby(loc.latitude, loc.longitude)
                }
                .onFailure {
                    Snackbar.make(
                        binding.root,
                        "Не удалось определить координаты. Выберите город вручную.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { renderState(it) }
            }
        }
    }

    private fun observeSelection() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selected.collect { city ->
                    setFragmentResult(
                        RESULT_KEY,
                        bundleOf(
                            ARG_CITY_ID to city.id,
                            ARG_CITY_NAME to city.name,
                            ARG_CITY_REGION to city.region
                        )
                    )
                    dismiss()
                }
            }
        }
    }

    private fun renderState(state: CitySelectionState) {
        binding.progress.visibility = if (state is CitySelectionState.Loading) View.VISIBLE else View.GONE
        binding.textEmpty.visibility = View.GONE

        val items = mutableListOf<CityListItem>()
        when (state) {
            is CitySelectionState.Loading -> {
                adapter.submitList(emptyList())
                return
            }
            is CitySelectionState.Overview -> {
                if (state.recent.isNotEmpty()) {
                    items += CityListItem.Header("Недавние")
                    items += state.recent.map { CityListItem.CityRow(it) }
                }
                if (state.popular.isNotEmpty()) {
                    items += CityListItem.Header("Популярные")
                    items += state.popular.map { CityListItem.CityRow(it) }
                }
                if (items.isEmpty()) {
                    binding.textEmpty.text = "Список городов пуст"
                    binding.textEmpty.visibility = View.VISIBLE
                }
            }
            is CitySelectionState.Search -> {
                items += state.results.map { CityListItem.CityRow(it) }
            }
            is CitySelectionState.Nearby -> {
                items += CityListItem.Header("Ближайшие")
                items += state.results.map { CityListItem.CityRow(it, showDistance = true) }
            }
            is CitySelectionState.Empty -> {
                binding.textEmpty.text = "Ничего не найдено"
                binding.textEmpty.visibility = View.VISIBLE
            }
            is CitySelectionState.Error -> {
                binding.textEmpty.text = state.message
                binding.textEmpty.visibility = View.VISIBLE
            }
        }
        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
