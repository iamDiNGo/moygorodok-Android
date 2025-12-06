package com.gorod.moygorodok.ui.notification_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.databinding.FragmentNotificationListBinding

class NotificationListFragment : Fragment() {

    private var _binding: FragmentNotificationListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationListViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationListBinding.inflate(inflater, container, false)
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

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                else -> {
                    viewModel.markAllAsRead()
                    true
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = NotificationAdapter(
            onNotificationClick = { notification ->
                viewModel.markAsRead(notification.id)
                // Handle navigation based on notification type
            },
            onDeleteClick = { notification ->
                viewModel.deleteNotification(notification.id)
            }
        )

        binding.recyclerNotifications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NotificationListFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            adapter.submitList(notifications)
            binding.layoutEmpty.visibility = if (notifications.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerNotifications.visibility = if (notifications.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.unreadCount.observe(viewLifecycleOwner) { count ->
            binding.textUnreadCount.text = if (count > 0) {
                "$count непрочитанных"
            } else {
                "Все прочитано"
            }
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
