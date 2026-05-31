package com.gorod.moygorodok.ui.announcements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.AnnouncementStatus
import com.gorod.moygorodok.databinding.FragmentMyAnnouncementsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class MyAnnouncementsFragment : Fragment() {

    private var _binding: FragmentMyAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyAnnouncementsViewModel by viewModels()
    private lateinit var adapter: AnnouncementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAnnouncementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecycler()
        setupTabs()
        setupSwipeRefresh()
        setupFab()
        observeViewModel()
        viewModel.loadFirstPage()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.items.value?.isNotEmpty() == true) {
            viewModel.refresh()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecycler() {
        adapter = AnnouncementAdapter { announcement ->
            val bundle = Bundle().apply { putInt("announcementId", announcement.id) }
            findNavController().navigate(R.id.navigation_announcement_detail, bundle)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAnnouncements.layoutManager = layoutManager
        binding.recyclerAnnouncements.adapter = adapter

        binding.recyclerAnnouncements.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                viewModel.loadMoreIfNeeded(lastVisible)
            }
        })
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val status = when (tab.position) {
                    1 -> AnnouncementStatus.PUBLISHED
                    2 -> AnnouncementStatus.EXPIRED
                    3 -> AnnouncementStatus.CLOSED
                    else -> null
                }
                viewModel.setStatus(status)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
    }

    private fun setupFab() {
        binding.fabCreate.setOnClickListener {
            val bundle = Bundle().apply { putInt("editId", -1) }
            findNavController().navigate(R.id.navigation_create_announcement, bundle)
        }
    }

    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.textEmpty.visibility =
                if (items.isEmpty() && viewModel.isLoading.value != true) View.VISIBLE else View.GONE
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.isRefreshing.observe(viewLifecycleOwner) { refreshing ->
            binding.swipeRefresh.isRefreshing = refreshing
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
