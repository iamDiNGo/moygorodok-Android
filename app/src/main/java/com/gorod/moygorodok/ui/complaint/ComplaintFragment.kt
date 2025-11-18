package com.gorod.moygorodok.ui.complaint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.ComplaintCategory
import com.gorod.moygorodok.databinding.FragmentComplaintBinding

class ComplaintFragment : Fragment() {

    private var _binding: FragmentComplaintBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComplaintViewModel by viewModels()
    private lateinit var categoryAdapter: ComplaintCategoryAdapter
    private lateinit var imageAdapter: AttachedImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComplaintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupCategoryAdapter()
        setupImageAdapter()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupCategoryAdapter() {
        categoryAdapter = ComplaintCategoryAdapter { category ->
            viewModel.selectCategory(category)
        }

        binding.recyclerCategories.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = categoryAdapter
        }
    }

    private fun setupImageAdapter() {
        imageAdapter = AttachedImageAdapter(
            onRemoveClick = { position ->
                viewModel.removeImage(position)
            }
        )

        binding.recyclerImages.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }

    private fun setupClickListeners() {
        binding.buttonAddImage.setOnClickListener {
            // Simulate adding image (in real app would use image picker)
            val mockImageColors = listOf("#FFE0E0", "#E0FFE0", "#E0E0FF", "#FFFFE0", "#FFE0FF")
            val currentCount = viewModel.attachedImages.value?.size ?: 0
            if (currentCount < 5) {
                viewModel.addImage(mockImageColors[currentCount % mockImageColors.size])
            } else {
                Toast.makeText(context, "Максимум 5 изображений", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSubmit.setOnClickListener {
            submitComplaint()
        }
    }

    private fun submitComplaint() {
        val title = binding.editTitle.text.toString()
        val description = binding.editDescription.text.toString()
        val address = binding.editAddress.text.toString()
        val name = binding.editName.text.toString()
        val phone = binding.editPhone.text.toString()

        if (viewModel.selectedCategory.value == null) {
            Toast.makeText(context, "Выберите категорию обращения", Toast.LENGTH_SHORT).show()
            return
        }

        if (title.isBlank()) {
            binding.layoutTitle.error = "Введите заголовок"
            return
        } else {
            binding.layoutTitle.error = null
        }

        if (description.isBlank()) {
            binding.layoutDescription.error = "Опишите проблему"
            return
        } else {
            binding.layoutDescription.error = null
        }

        if (address.isBlank()) {
            binding.layoutAddress.error = "Укажите адрес"
            return
        } else {
            binding.layoutAddress.error = null
        }

        val success = viewModel.submitComplaint(title, description, address, name, phone)
        if (success) {
            Toast.makeText(context, "Обращение отправлено!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { selected ->
            categoryAdapter.setSelectedCategory(selected)
        }

        viewModel.attachedImages.observe(viewLifecycleOwner) { images ->
            imageAdapter.submitList(images)
            binding.textImageCount.text = "${images.size}/5"
            binding.recyclerImages.visibility = if (images.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.buttonSubmit.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
