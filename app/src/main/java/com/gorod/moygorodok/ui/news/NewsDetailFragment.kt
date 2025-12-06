package com.gorod.moygorodok.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gorod.moygorodok.databinding.FragmentNewsDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class NewsDetailFragment : Fragment() {

    private var _binding: FragmentNewsDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsDetailViewModel by viewModels()

    private val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        observeViewModel()

        val newsId = arguments?.getString("newsId")
        if (newsId != null) {
            viewModel.loadNews(newsId)
        } else {
            Snackbar.make(binding.root, "Ошибка: ID новости не найден", Snackbar.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.news.observe(viewLifecycleOwner) { news ->
            news?.let {
                binding.textTitle.text = it.title
                binding.textContent.text = it.content
                binding.textCategory.text = it.category.displayName
                binding.textDate.text = dateFormat.format(it.publishedAt)
                binding.textAuthor.text = it.author
                binding.textViews.text = "${it.viewCount} просмотров"

                binding.toolbar.title = it.category.displayName

                // Если есть изображение
                if (it.imageUrl != null) {
                    binding.imageNews.visibility = View.VISIBLE
                    // Загрузка изображения через Glide/Coil
                } else {
                    binding.imageNews.visibility = View.GONE
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentGroup.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("Назад") {
                        findNavController().navigateUp()
                    }
                    .show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
