package com.gorod.moygorodok.ui.announcements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.AnnouncementCategory
import com.gorod.moygorodok.databinding.FragmentCreateAnnouncementBinding
import com.gorod.moygorodok.databinding.ViewAnnouncementPhotoTileBinding
import com.google.android.material.snackbar.Snackbar

class CreateAnnouncementFragment : Fragment() {

    private var _binding: FragmentCreateAnnouncementBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateAnnouncementViewModel by viewModels()

    private val editId: Int
        get() = arguments?.getInt("editId", -1) ?: -1

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(CreateAnnouncementViewModel.MAX_PHOTOS)
    ) { uris ->
        if (uris.isNotEmpty()) viewModel.addNewPhotos(uris)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupCategoryButton()
        setupNegotiableSwitch()
        setupSubmit()
        observeViewModel()

        if (editId > 0) {
            binding.toolbar.setTitle(R.string.title_edit_announcement)
            binding.buttonSubmit.setText(R.string.announcement_submit_save)
            viewModel.loadForEditing(editId)
        }

        binding.textPhotosLabel.text =
            getString(R.string.announcement_field_photos, CreateAnnouncementViewModel.MAX_PHOTOS)
        rebuildPhotos()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun setupCategoryButton() {
        binding.buttonCategory.setOnClickListener { anchor ->
            val popup = PopupMenu(requireContext(), anchor)
            AnnouncementCategory.entries.forEachIndexed { index, category ->
                popup.menu.add(0, index, index, category.label)
            }
            popup.setOnMenuItemClickListener { item ->
                val category = AnnouncementCategory.entries[item.itemId]
                viewModel.category = category
                applyCategoryToButton(category)
                true
            }
            popup.show()
        }
        viewModel.category?.let { applyCategoryToButton(it) }
    }

    private fun applyCategoryToButton(category: AnnouncementCategory) {
        binding.buttonCategory.text = category.label
        binding.buttonCategory.setIconResource(AnnouncementCategoryIcons.iconRes(category))
    }

    private fun setupNegotiableSwitch() {
        binding.switchNegotiable.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isNegotiable = isChecked
            binding.editPrice.isEnabled = !isChecked
            binding.inputLayoutPrice.alpha = if (isChecked) 0.5f else 1f
            if (isChecked) binding.editPrice.setText("")
        }
    }

    private fun setupSubmit() {
        binding.buttonSubmit.setOnClickListener {
            viewModel.submit(
                title = binding.editTitle.text?.toString().orEmpty(),
                description = binding.editDescription.text?.toString().orEmpty(),
                priceText = binding.editPrice.text?.toString().orEmpty(),
                address = binding.editAddress.text?.toString()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.isSubmitting.observe(viewLifecycleOwner) { submitting ->
            binding.buttonSubmit.isEnabled = !submitting
            binding.progressBar.visibility = if (submitting) View.VISIBLE else View.GONE
        }
        viewModel.existingPhotos.observe(viewLifecycleOwner) {
            rebuildPhotos()
            prefillFromEditingIfNeeded()
        }
        viewModel.newPhotos.observe(viewLifecycleOwner) { rebuildPhotos() }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        viewModel.result.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            if (result.failedPhotos > 0) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.announcement_photos_partial_failure, result.failedPhotos, result.totalNewPhotos),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            viewModel.consumeResult()
            // Возврат к списку (List сам сделает refresh в onResume)
            findNavController().navigateUp()
        }

    }

    private fun prefillFromEditingIfNeeded() {
        val a = viewModel.editingAnnouncement ?: return
        if (!binding.editTitle.text.isNullOrEmpty()) return
        binding.editTitle.setText(a.title)
        binding.editDescription.setText(a.description.orEmpty())
        binding.editAddress.setText(a.address.orEmpty())
        a.price?.let { binding.editPrice.setText(it.toLong().toString()) }
        binding.switchNegotiable.isChecked = (a.price == null)
        viewModel.category?.let { applyCategoryToButton(it) }
    }

    private fun rebuildPhotos() {
        val container = binding.photosContainer
        container.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())

        // Существующие фото
        for (photo in viewModel.existingPhotos.value.orEmpty()) {
            val tile = ViewAnnouncementPhotoTileBinding.inflate(inflater, container, false)
            tile.imageTile.visibility = View.VISIBLE
            tile.imageAdd.visibility = View.GONE
            tile.buttonRemove.visibility = View.VISIBLE
            tile.imageTile.load(photo.thumbnailUrl ?: photo.url) {
                crossfade(true)
            }
            tile.buttonRemove.setOnClickListener { viewModel.removeExistingPhoto(photo.id) }
            container.addView(tile.root)
        }

        // Новые фото
        viewModel.newPhotos.value.orEmpty().forEachIndexed { index, uri ->
            val tile = ViewAnnouncementPhotoTileBinding.inflate(inflater, container, false)
            tile.imageTile.visibility = View.VISIBLE
            tile.imageAdd.visibility = View.GONE
            tile.buttonRemove.visibility = View.VISIBLE
            tile.imageTile.load(uri) { crossfade(true) }
            tile.buttonRemove.setOnClickListener { viewModel.removeNewPhoto(index) }
            container.addView(tile.root)
        }

        // «+» тайл
        if (viewModel.totalVisiblePhotos() < CreateAnnouncementViewModel.MAX_PHOTOS) {
            val tile = ViewAnnouncementPhotoTileBinding.inflate(inflater, container, false)
            tile.imageTile.visibility = View.GONE
            tile.imageAdd.visibility = View.VISIBLE
            tile.buttonRemove.visibility = View.GONE
            tile.root.setOnClickListener { pickPhotos() }
            container.addView(tile.root)
        }
    }

    private fun pickPhotos() {
        val available = CreateAnnouncementViewModel.MAX_PHOTOS - viewModel.totalVisiblePhotos()
        if (available <= 0) {
            Snackbar.make(binding.root, R.string.announcement_photos_limit_reached, Snackbar.LENGTH_SHORT).show()
            return
        }
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

