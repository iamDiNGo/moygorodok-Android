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
        binding.buttonRetry.setOnClickListener { viewModel.refresh() }
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapters() {
        mainAdapter = MainEmergencyAdapter { contact -> callNumber(contact) }
        allAdapter = EmergencyAdapter { contact -> callNumber(contact) }

        binding.recyclerMainContacts.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = mainAdapter
        }

        binding.recyclerAllContacts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = allAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.mainContacts.observe(viewLifecycleOwner) { contacts ->
            val hasContacts = contacts.isNotEmpty()
            binding.textQuickDialTitle.visibility = if (hasContacts) View.VISIBLE else View.GONE
            binding.recyclerMainContacts.visibility = if (hasContacts) View.VISIBLE else View.GONE

            if (hasContacts) {
                val spanCount = contacts.size.coerceIn(1, 4)
                binding.recyclerMainContacts.layoutManager = GridLayoutManager(context, spanCount)
                mainAdapter.submitList(contacts)
            }
        }

        viewModel.groupedContacts.observe(viewLifecycleOwner) { groups ->
            val rows = buildList<EmergencyAdapter.Row> {
                groups.forEach { (category, contacts) ->
                    val first = contacts.first()
                    add(EmergencyAdapter.Row.Header(category, first.color, first.iconKey))
                    contacts.forEach { add(EmergencyAdapter.Row.Contact(it)) }
                }
            }
            allAdapter.submitList(rows)
            binding.textAllTitle.visibility = if (groups.isNotEmpty()) View.VISIBLE else View.GONE
            binding.recyclerAllContacts.visibility = if (groups.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            renderState(
                loading = loading,
                error = viewModel.error.value,
                hasData = !viewModel.contacts.value.isNullOrEmpty()
            )
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            renderState(
                loading = viewModel.isLoading.value == true,
                error = error,
                hasData = !viewModel.contacts.value.isNullOrEmpty()
            )
        }
        viewModel.contacts.observe(viewLifecycleOwner) { list ->
            renderState(
                loading = viewModel.isLoading.value == true,
                error = viewModel.error.value,
                hasData = list.isNotEmpty()
            )
        }
    }

    private fun renderState(loading: Boolean, error: String?, hasData: Boolean) {
        val showLoading = loading && !hasData
        val showError = !loading && error != null && !hasData
        val showEmpty = !loading && error == null && !hasData

        binding.stateContainer.visibility =
            if (showLoading || showError || showEmpty) View.VISIBLE else View.GONE
        binding.progress.visibility = if (showLoading) View.VISIBLE else View.GONE
        binding.errorView.visibility = if (showError) View.VISIBLE else View.GONE
        binding.textEmpty.visibility = if (showEmpty) View.VISIBLE else View.GONE

        if (showError) {
            binding.textError.text = error
        }
    }

    private fun callNumber(contact: EmergencyContact) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${contact.phoneNormalized}")
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
