package com.gorod.moygorodok.ui.report.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.Report
import com.gorod.moygorodok.data.model.ReportStatus
import com.gorod.moygorodok.databinding.FragmentReportDetailBinding
import com.gorod.moygorodok.ui.report.colorResFor
import com.gorod.moygorodok.ui.report.iconResFor
import com.gorod.moygorodok.util.ReportDateFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ReportDetailFragment : Fragment() {

    private var _binding: FragmentReportDetailBinding? = null
    private val binding get() = _binding!!

    private val reportId: Int
        get() = requireArguments().getInt("reportId")

    private val viewModel: ReportDetailViewModel by viewModels {
        ReportDetailViewModel.factory(reportId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonDelete.setOnClickListener { confirmDelete() }

        observe()
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        binding.progressBar.isVisible = state is ReportDetailViewModel.State.Loading
                        when (state) {
                            is ReportDetailViewModel.State.Data -> render(state.report)
                            is ReportDetailViewModel.State.Error -> {
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            }
                            ReportDetailViewModel.State.Loading -> Unit
                        }
                    }
                }
                launch {
                    viewModel.isProcessing.collect { processing ->
                        binding.buttonDelete.isEnabled = !processing
                    }
                }
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            ReportDetailViewModel.Event.Deleted -> {
                                Snackbar.make(
                                    requireActivity().findViewById(android.R.id.content),
                                    R.string.report_detail_delete_success,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                findNavController().navigateUp()
                            }
                            is ReportDetailViewModel.Event.Error -> {
                                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun render(report: Report) {
        val ctx = requireContext()

        binding.imageReason.setImageResource(iconResFor(report.reason))
        binding.textReason.text = report.reasonLabel
        binding.textPreview.text = report.reportablePreview?.takeIf { it.isNotBlank() }
            ?: report.reportableType.apiValue

        binding.chipStatus.text = report.statusLabel
        val statusColor = ContextCompat.getColor(ctx, colorResFor(report.status))
        binding.chipStatus.chipBackgroundColor = ColorStateList.valueOf(statusColor)
        binding.chipStatus.setTextColor(android.graphics.Color.WHITE)

        binding.textComment.text = report.comment?.takeIf { it.isNotBlank() }
            ?: getString(R.string.report_detail_no_comment)

        binding.textCreatedAt.text = "Отправлено: " + ReportDateFormatter.formatLong(report.createdAt)

        val resolutionParts = listOfNotNull(
            report.resolutionActionLabel?.takeIf { it.isNotBlank() },
            report.reviewedAt?.let { ReportDateFormatter.formatLong(it) }?.takeIf { it.isNotBlank() }
        )
        if (resolutionParts.isNotEmpty() && report.status != ReportStatus.PENDING) {
            binding.resolutionBlock.isVisible = true
            binding.textResolution.text = resolutionParts.joinToString(" · ")
        } else {
            binding.resolutionBlock.isVisible = false
        }

        binding.buttonDelete.isVisible = report.status == ReportStatus.PENDING

        val photos = report.photos.orEmpty()
        if (photos.isEmpty()) {
            binding.photoPager.isVisible = false
            binding.textPhotoCounter.isVisible = false
        } else {
            binding.photoPager.isVisible = true
            binding.photoPager.adapter = ReportPhotoPagerAdapter(photos)
            binding.textPhotoCounter.isVisible = photos.size > 1
            binding.textPhotoCounter.text = "1 / ${photos.size}"
            binding.photoPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.textPhotoCounter.text = "${position + 1} / ${photos.size}"
                }
            })
        }
    }

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.report_detail_delete_confirm_title)
            .setMessage(R.string.report_detail_delete_confirm_message)
            .setPositiveButton(R.string.report_detail_delete) { _, _ -> viewModel.delete() }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
