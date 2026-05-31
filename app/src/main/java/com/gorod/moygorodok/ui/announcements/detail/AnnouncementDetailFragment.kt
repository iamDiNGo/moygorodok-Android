package com.gorod.moygorodok.ui.announcements.detail

import com.gorod.moygorodok.ui.announcements.common.AnnouncementCategoryIcons

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.Announcement
import com.gorod.moygorodok.data.model.AnnouncementStatus
import com.gorod.moygorodok.data.model.ReportableType
import com.gorod.moygorodok.data.repository.AuthRepository
import com.gorod.moygorodok.databinding.FragmentAnnouncementDetailBinding
import com.gorod.moygorodok.ui.report.sheet.ReportBottomSheetFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class AnnouncementDetailFragment : Fragment() {

    private var _binding: FragmentAnnouncementDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnnouncementDetailViewModel by viewModels()
    private lateinit var photoAdapter: AnnouncementPhotoPagerAdapter
    private var tabMediator: TabLayoutMediator? = null

    private val announcementId: Int
        get() = arguments?.getInt("announcementId") ?: -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnouncementDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (announcementId <= 0) {
            Snackbar.make(binding.root, "Ошибка: ID объявления не передан", Snackbar.LENGTH_LONG).show()
            findNavController().navigateUp()
            return
        }

        setupToolbar()
        setupPhotoPager()
        observeViewModel()
        viewModel.load(announcementId)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.announcement.value != null) {
            // тихий refresh при возврате после Edit/Renew/Close
            viewModel.refresh()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_announcement_detail)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.toolbar.setOnMenuItemClickListener(::onMenuItemClick)
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        val current = viewModel.announcement.value ?: return false
        return when (item.itemId) {
            R.id.action_favorite -> {
                if (requireLogin()) viewModel.toggleFavorite()
                true
            }
            R.id.action_share -> {
                shareAnnouncement(current)
                true
            }
            R.id.action_report -> {
                if (requireLogin()) {
                    ReportBottomSheetFragment.newInstance(
                        type = ReportableType.CLASSIFIED,
                        id = current.id,
                        title = current.title
                    ).show(parentFragmentManager, "report_sheet")
                }
                true
            }
            R.id.action_edit -> {
                openEdit(current.id)
                true
            }
            R.id.action_renew -> {
                viewModel.renew()
                true
            }
            R.id.action_close -> {
                confirmClose()
                true
            }
            R.id.action_delete -> {
                confirmDelete()
                true
            }
            else -> false
        }
    }

    private fun setupPhotoPager() {
        photoAdapter = AnnouncementPhotoPagerAdapter(R.drawable.ic_category_other)
        binding.photoPager.adapter = photoAdapter
    }

    private fun observeViewModel() {
        viewModel.announcement.observe(viewLifecycleOwner) { announcement ->
            announcement?.let { render(it) }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.contentGroup.visibility = if (loading) View.INVISIBLE else View.VISIBLE
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        viewModel.closed.observe(viewLifecycleOwner) { closed ->
            if (closed) findNavController().navigateUp()
        }
    }

    private fun render(a: Announcement) {
        val placeholderRes = AnnouncementCategoryIcons.iconRes(a.category)

        // Фото
        val photos = a.photos.orEmpty()
        if (photos.isNotEmpty()) {
            binding.photoPager.visibility = View.VISIBLE
            binding.imagePlaceholder.visibility = View.GONE
            photoAdapter.submit(photos)
            tabMediator?.detach()
            tabMediator = TabLayoutMediator(binding.photoIndicator, binding.photoPager) { _, _ -> }
                .also { it.attach() }
            binding.photoIndicator.visibility = if (photos.size > 1) View.VISIBLE else View.GONE
        } else {
            binding.photoPager.visibility = View.GONE
            binding.imagePlaceholder.visibility = View.VISIBLE
            binding.imagePlaceholder.setImageResource(placeholderRes)
            binding.photoIndicator.visibility = View.GONE
        }

        // Status banner
        when (a.status) {
            AnnouncementStatus.EXPIRED -> {
                binding.textStatusBanner.visibility = View.VISIBLE
                binding.textStatusBanner.text = getString(R.string.announcement_status_expired)
                binding.textStatusBanner.setBackgroundResource(R.drawable.bg_badge_urgent)
                binding.textStatusBanner.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
            AnnouncementStatus.CLOSED -> {
                binding.textStatusBanner.visibility = View.VISIBLE
                binding.textStatusBanner.text = getString(R.string.announcement_status_closed)
                binding.textStatusBanner.setBackgroundResource(R.drawable.bg_badge_closed)
                binding.textStatusBanner.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
            else -> binding.textStatusBanner.visibility = View.GONE
        }

        // Контент
        binding.textPrice.text = a.priceFormatted
        binding.textTitle.text = a.title
        binding.chipCategory.text = a.categoryLabel
        binding.textDate.text = a.timeAgo()
        binding.textViews.text = getString(R.string.announcement_views_count_long, a.viewsCount)
        binding.textDescription.text = a.description.orEmpty()

        // Адрес
        if (!a.address.isNullOrBlank()) {
            binding.labelLocation.visibility = View.VISIBLE
            binding.cardLocation.visibility = View.VISIBLE
            binding.textAddress.text = a.address
        } else {
            binding.labelLocation.visibility = View.GONE
            binding.cardLocation.visibility = View.GONE
        }

        // Автор
        binding.textAuthorName.text = a.author?.name ?: "—"
        val phone = a.author?.phone
        binding.textAuthorPhone.text = when {
            !phone.isNullOrBlank() -> phone
            isLoggedIn() -> getString(R.string.announcement_phone_hidden_auth)
            else -> getString(R.string.announcement_phone_hidden_guest)
        }
        val avatarUrl = a.author?.avatarUrl
        if (!avatarUrl.isNullOrBlank()) {
            binding.imageAuthorAvatar.load(avatarUrl) {
                placeholder(R.drawable.ic_person)
                error(R.drawable.ic_person)
                crossfade(true)
            }
        } else {
            binding.imageAuthorAvatar.setImageResource(R.drawable.ic_person)
        }

        // Кнопки и меню
        val isOwner = a.isOwner == true
        binding.buttonCall.visibility = if (isOwner) View.GONE else View.VISIBLE
        binding.buttonEdit.visibility = if (isOwner) View.VISIBLE else View.GONE
        binding.buttonCall.setOnClickListener { handleCallClick(a) }
        binding.buttonEdit.setOnClickListener { openEdit(a.id) }

        updateMenu(a)
    }

    private fun updateMenu(a: Announcement) {
        val menu = binding.toolbar.menu
        val isOwner = a.isOwner == true
        val loggedIn = isLoggedIn()

        // Heart: только не-владелец + auth
        val favItem = menu.findItem(R.id.action_favorite)
        if (isOwner || !loggedIn) {
            favItem?.isVisible = false
        } else {
            favItem?.isVisible = true
            val icon = if (a.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
            favItem?.setIcon(icon)
        }

        menu.findItem(R.id.action_share)?.isVisible = true
        menu.findItem(R.id.action_report)?.isVisible = !isOwner && loggedIn
        menu.findItem(R.id.action_edit)?.isVisible = isOwner
        menu.findItem(R.id.action_close)?.isVisible = isOwner && a.status == AnnouncementStatus.PUBLISHED
        menu.findItem(R.id.action_renew)?.isVisible = isOwner &&
            (a.status == AnnouncementStatus.EXPIRED || a.status == AnnouncementStatus.CLOSED)
        menu.findItem(R.id.action_delete)?.isVisible = isOwner
    }

    private fun handleCallClick(a: Announcement) {
        val phone = a.author?.phone
        if (!phone.isNullOrBlank()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.announcement_call_dialog_title)
                .setMessage(phone)
                .setPositiveButton(R.string.announcement_call_dialog_yes) { _, _ -> openDialer(phone) }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        } else if (!isLoggedIn()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.announcement_login_required_title)
                .setMessage(R.string.announcement_call_login_required_message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    private fun openDialer(phone: String) {
        val normalized = phone.filter { it == '+' || it.isDigit() }
        val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:$normalized") }
        startActivity(intent)
    }

    private fun shareAnnouncement(a: Announcement) {
        val text = "${a.title}\n${a.priceFormatted}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.button_share)))
    }

    private fun openEdit(id: Int) {
        val bundle = Bundle().apply { putInt("editId", id) }
        findNavController().navigate(R.id.navigation_create_announcement, bundle)
    }

    private fun confirmClose() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.announcement_action_close_confirm_title)
            .setMessage(R.string.announcement_action_close_confirm_message)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.announcement_action_close_confirm_yes) { _, _ -> viewModel.close() }
            .show()
    }

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.announcement_action_delete_confirm_title)
            .setMessage(R.string.announcement_action_delete_confirm_message)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.announcement_action_delete) { _, _ -> viewModel.delete() }
            .show()
    }

    private fun isLoggedIn(): Boolean =
        AuthRepository.getInstance(requireContext()).isLoggedIn()

    private fun requireLogin(): Boolean {
        if (isLoggedIn()) return true
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.announcement_login_required_title)
            .setMessage(R.string.announcement_login_required_message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator?.detach()
        tabMediator = null
        _binding = null
    }
}
