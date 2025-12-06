package com.gorod.moygorodok.ui.emergency

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.data.model.EmergencyCategory
import com.gorod.moygorodok.data.model.EmergencyContact
import com.gorod.moygorodok.databinding.FragmentEmergencyBinding

class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EmergencyViewModel by viewModels()
    private lateinit var mainAdapter: MainEmergencyAdapter
    private lateinit var allAdapter: EmergencyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapters()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapters() {
        mainAdapter = MainEmergencyAdapter { contact ->
            callNumber(contact.phone)
        }

        allAdapter = EmergencyAdapter { contact ->
            callNumber(contact.phone)
        }

        binding.recyclerMainContacts.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mainAdapter
        }

        binding.recyclerAllContacts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = allAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.mainContacts.observe(viewLifecycleOwner) { contacts ->
            mainAdapter.submitList(contacts)
        }

        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            // Filter out main contacts for the "all" list
            val otherContacts = contacts.filter { !it.isMainNumber }
            allAdapter.submitList(otherContacts)
        }
    }

    private fun callNumber(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
