package com.gorod.moygorodok.ui.announcements.favorites

import com.gorod.moygorodok.ui.announcements.common.AnnouncementAdapter

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
import com.gorod.moygorodok.databinding.FragmentFavoriteAnnouncementsBinding
import com.google.android.material.snackbar.Snackbar

class FavoriteAnnouncementsFragment : Fragment() {

    private var _binding: FragmentFavoriteAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteAnnouncementsViewModel by viewModels()
    private lateinit var adapter: AnnouncementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteAnnouncementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

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

        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }

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

        viewModel.loadFirstPage()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.items.value?.isNotEmpty() == true) {
            viewModel.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
