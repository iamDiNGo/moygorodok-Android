package com.gorod.moygorodok.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapter()
        setupInput()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapter() {
        adapter = ChatAdapter()

        binding.recyclerMessages.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = this@ChatFragment.adapter
        }
    }

    private fun setupInput() {
        binding.buttonSend.setOnClickListener {
            val text = binding.editMessage.text.toString()
            if (text.isNotBlank()) {
                viewModel.sendMessage(text)
                binding.editMessage.text?.clear()
                scrollToBottom()
            }
        }
    }

    private fun scrollToBottom() {
        binding.recyclerMessages.postDelayed({
            val itemCount = adapter.itemCount
            if (itemCount > 0) {
                binding.recyclerMessages.smoothScrollToPosition(itemCount - 1)
            }
        }, 100)
    }

    private fun observeViewModel() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages) {
                scrollToBottom()
            }
        }

        viewModel.onlineCount.observe(viewLifecycleOwner) { count ->
            binding.textOnline.text = "$count онлайн"
        }

        viewModel.membersCount.observe(viewLifecycleOwner) { count ->
            binding.textMembers.text = "$count участников"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
