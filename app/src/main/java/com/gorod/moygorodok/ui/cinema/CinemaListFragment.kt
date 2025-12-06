package com.gorod.moygorodok.ui.cinema

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.databinding.FragmentCinemaListBinding

class CinemaListFragment : Fragment() {

    private var _binding: FragmentCinemaListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CinemaViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCinemaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapter()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapter() {
        adapter = MovieAdapter { movie ->
            val action = CinemaListFragmentDirections.actionCinemaListToMovieDetail(movie.id)
            findNavController().navigate(action)
        }

        binding.recyclerMovies.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CinemaListFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            adapter.submitList(movies)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
