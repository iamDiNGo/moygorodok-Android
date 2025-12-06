package com.gorod.moygorodok.ui.tasks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Task
import com.gorod.moygorodok.data.model.TaskPriceType
import com.gorod.moygorodok.databinding.ItemTaskBinding

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                textTitle.text = task.title
                textLocation.text = task.location
                textDate.text = task.date
                textCategory.text = "${task.category.emoji} ${task.category.displayName}"
                textResponses.text = "${task.responses} откликов"
                textViews.text = "${task.views} просмотров"

                // Price
                textPrice.text = when (task.price.type) {
                    TaskPriceType.FIXED -> "${task.price.amount?.toInt()} ₽"
                    TaskPriceType.NEGOTIABLE -> "Договорная"
                    TaskPriceType.HOURLY -> "${task.price.amount?.toInt()} ₽/час"
                    TaskPriceType.RANGE -> "${task.price.amount?.toInt()} - ${task.price.maxAmount?.toInt()} ₽"
                }

                // Urgent badge
                badgeUrgent.visibility = if (task.isUrgent) View.VISIBLE else View.GONE

                // Executor badge
                if (task.executor != null) {
                    badgeExecutor.visibility = View.VISIBLE
                    textExecutorName.text = task.executor.name
                    textExecutorRating.text = task.executor.rating.toString()
                } else {
                    badgeExecutor.visibility = View.GONE
                }

                // Image color placeholder
                try {
                    imageTask.setBackgroundColor(Color.parseColor(task.imageColor))
                } catch (e: Exception) {
                    imageTask.setBackgroundColor(Color.parseColor("#CCCCCC"))
                }

                root.setOnClickListener { onTaskClick(task) }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
