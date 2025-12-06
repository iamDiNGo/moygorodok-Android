package com.gorod.moygorodok.ui.tasks

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gorod.moygorodok.R
import com.gorod.moygorodok.data.model.Task
import com.gorod.moygorodok.data.model.TaskPriceType
import com.gorod.moygorodok.databinding.FragmentTaskDetailBinding

class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskDetailViewModel by viewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        observeViewModel()
        viewModel.loadTask(args.taskId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_favorite -> {
                    viewModel.toggleFavorite()
                    true
                }
                R.id.action_share -> {
                    shareTask()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.task.observe(viewLifecycleOwner) { task ->
            task?.let { bindTask(it) }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            val icon = if (isFavorite) {
                R.drawable.ic_favorite_filled
            } else {
                R.drawable.ic_favorite_border
            }
            binding.toolbar.menu.findItem(R.id.action_favorite)?.setIcon(icon)
        }
    }

    private fun bindTask(task: Task) {
        binding.apply {
            // Header image
            try {
                imageHeader.setBackgroundColor(Color.parseColor(task.imageColor))
            } catch (e: Exception) {
                imageHeader.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }

            // Basic info
            textTitle.text = task.title
            textCategory.text = "${task.category.emoji} ${task.category.displayName}"
            textLocation.text = task.location
            textAddress.text = task.address
            textDate.text = task.date
            task.time?.let {
                textTime.text = it
                textTime.visibility = View.VISIBLE
            } ?: run {
                textTime.visibility = View.GONE
            }

            // Price
            textPrice.text = when (task.price.type) {
                TaskPriceType.FIXED -> "${task.price.amount?.toInt()} ₽"
                TaskPriceType.NEGOTIABLE -> "Договорная"
                TaskPriceType.HOURLY -> "${task.price.amount?.toInt()} ₽/час"
                TaskPriceType.RANGE -> "${task.price.amount?.toInt()} - ${task.price.maxAmount?.toInt()} ₽"
            }

            // Stats
            textViews.text = "${task.views} просмотров"
            textResponses.text = "${task.responses} откликов"
            textCreatedAt.text = task.createdAt

            // Description
            textDescription.text = task.description

            // Urgent badge
            badgeUrgent.visibility = if (task.isUrgent) View.VISIBLE else View.GONE

            // Author info
            textAuthorName.text = task.authorName
            textAuthorPhone.text = task.authorPhone

            // Executor info
            if (task.executor != null) {
                cardExecutor.visibility = View.VISIBLE
                textExecutorName.text = task.executor.name
                textExecutorRating.text = task.executor.rating.toString()
                textExecutorReviews.text = "${task.executor.reviewCount} отзывов"
                textExecutorTasks.text = "${task.executor.completedTasks} заданий"

                try {
                    imageExecutorAvatar.setBackgroundColor(Color.parseColor(task.executor.avatarColor))
                } catch (e: Exception) {
                    imageExecutorAvatar.setBackgroundColor(Color.parseColor("#CCCCCC"))
                }
            } else {
                cardExecutor.visibility = View.GONE
            }

            // Action buttons
            buttonCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${task.authorPhone}")
                }
                startActivity(intent)
            }

            buttonRespond.setOnClickListener {
                // Respond to task action
            }

            buttonMessage.setOnClickListener {
                // Open chat with author
            }
        }
    }

    private fun shareTask() {
        viewModel.task.value?.let { task ->
            val shareText = "${task.title}\n${task.description}\nЦена: ${
                when (task.price.type) {
                    TaskPriceType.FIXED -> "${task.price.amount?.toInt()} ₽"
                    TaskPriceType.NEGOTIABLE -> "Договорная"
                    TaskPriceType.HOURLY -> "${task.price.amount?.toInt()} ₽/час"
                    TaskPriceType.RANGE -> "${task.price.amount?.toInt()} - ${task.price.maxAmount?.toInt()} ₽"
                }
            }"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Поделиться"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
