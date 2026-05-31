package com.gorod.moygorodok.ui.report.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.ReportableType
import com.gorod.moygorodok.data.repository.ReportError
import com.gorod.moygorodok.databinding.BottomSheetReportBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ReportBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportSheetViewModel by viewModels {
        val type = ReportableType.valueOf(
            requireArguments().getString(ARG_TYPE)!!
        )
        ReportSheetViewModel.factory(
            reportableType = type,
            reportableId = requireArguments().getInt(ARG_ID),
            reportableTitle = requireArguments().getString(ARG_TITLE).orEmpty()
        )
    }

    private lateinit var reasonAdapter: ReportReasonAdapter
    private lateinit var screenshotAdapter: ReportScreenshotAdapter

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(ReportSheetViewModel.MAX_PHOTOS)
    ) { uris ->
        viewModel.addScreenshots(uris)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textTarget.text = getString(
            R.string.report_sheet_target_prefix
        ) + " «" + viewModel.reportableTitle + "»"

        setupReasons()
        setupScreenshots()
        setupComment()
        setupSubmit()
        observe()
    }

    private fun setupReasons() {
        reasonAdapter = ReportReasonAdapter { reason ->
            viewModel.selectReason(reason)
        }
        binding.recyclerReasons.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerReasons.adapter = reasonAdapter
        reasonAdapter.submitList(viewModel.reasons)
    }

    private fun setupScreenshots() {
        screenshotAdapter = ReportScreenshotAdapter { position ->
            viewModel.removeScreenshot(position)
        }
        binding.recyclerScreenshots.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerScreenshots.adapter = screenshotAdapter

        binding.buttonAddScreenshot.setOnClickListener {
            if (!viewModel.canAddMoreScreenshots) {
                Snackbar.make(binding.root, R.string.report_error_photos_limit, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    private fun setupComment() {
        binding.editComment.doAfterTextChanged {
            viewModel.setComment(it?.toString().orEmpty())
        }
    }

    private fun setupSubmit() {
        binding.buttonSubmit.setOnClickListener {
            viewModel.submit(requireContext())
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.selectedReason.collect { reason ->
                        reasonAdapter.setSelected(reason)
                        updateSubmitState()
                        binding.inputLayoutComment.helperText =
                            if (reason?.requiresComment() == true)
                                getString(R.string.report_comment_required_for_other)
                            else null
                    }
                }
                launch {
                    viewModel.comment.collect { updateSubmitState() }
                }
                launch {
                    viewModel.screenshots.collect { list ->
                        screenshotAdapter.submitList(list)
                        binding.buttonAddScreenshot.isVisible = list.size < ReportSheetViewModel.MAX_PHOTOS
                    }
                }
                launch {
                    viewModel.state.collect { state ->
                        val loading = state is ReportSheetViewModel.SubmitState.Loading
                        binding.progressBar.isVisible = loading
                        binding.buttonSubmit.isEnabled = !loading && viewModel.canSubmit()
                    }
                }
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            ReportSheetViewModel.Event.Submitted -> {
                                Snackbar.make(
                                    requireActivity().findViewById(android.R.id.content),
                                    R.string.report_submit_success,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                dismissAllowingStateLoss()
                            }
                            is ReportSheetViewModel.Event.Error -> {
                                val messageRes = when (event.throwable) {
                                    is ReportError.Duplicate -> R.string.report_error_duplicate
                                    is ReportError.OwnContent -> R.string.report_error_own_content
                                    is ReportError.RateLimited -> R.string.report_error_rate_limited
                                    is ReportError.PhotosLimit -> R.string.report_error_photos_limit
                                    else -> R.string.report_error_generic
                                }
                                Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateSubmitState() {
        binding.buttonSubmit.isEnabled = viewModel.canSubmit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ReportBottomSheet"
        private const val ARG_TYPE = "arg_type"
        private const val ARG_ID = "arg_id"
        private const val ARG_TITLE = "arg_title"

        fun newInstance(
            type: ReportableType,
            id: Int,
            title: String
        ): ReportBottomSheetFragment = ReportBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TYPE, type.name)
                putInt(ARG_ID, id)
                putString(ARG_TITLE, title)
            }
        }
    }
}
