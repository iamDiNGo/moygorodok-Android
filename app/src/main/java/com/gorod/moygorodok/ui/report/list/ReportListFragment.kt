package com.gorod.moygorodok.ui.report.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.databinding.FragmentReportListBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ReportListFragment : Fragment() {

    private var _binding: FragmentReportListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportListViewModel by viewModels()
    private lateinit var adapter: ReportListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = ReportListAdapter { report ->
            val args = Bundle().apply { putInt("reportId", report.id) }
            findNavController().navigate(R.id.navigation_report_detail, args)
        }
        binding.recyclerReports.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerReports.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        observe()
    }

    override fun onResume() {
        super.onResume()
        viewModel.load(initial = false)
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        binding.progressBar.isVisible = state is ReportListViewModel.State.Loading
                        binding.emptyState.isVisible = state is ReportListViewModel.State.Empty
                        when (state) {
                            is ReportListViewModel.State.Data -> {
                                adapter.submitList(state.reports)
                                binding.recyclerReports.isVisible = true
                            }
                            is ReportListViewModel.State.Empty -> {
                                adapter.submitList(emptyList())
                                binding.recyclerReports.isVisible = false
                            }
                            is ReportListViewModel.State.Loading -> {
                                binding.recyclerReports.isVisible = false
                            }
                            is ReportListViewModel.State.Error -> {
                                binding.recyclerReports.isVisible = false
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.isRefreshing.collect { binding.swipeRefresh.isRefreshing = it }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
