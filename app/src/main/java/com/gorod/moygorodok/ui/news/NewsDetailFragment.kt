package com.gorod.moygorodok.ui.news

import android.content.Intent
import android.net.Uri
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

    private val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ROOT)
    private val displayFormat = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru", "RU"))

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

        val newsId = arguments?.getString("newsId")?.toIntOrNull()
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
            news ?: return@observe
            binding.textTitle.text = news.title
            binding.textContent.text = news.content?.takeIf { it.isNotBlank() }
                ?: news.summary.orEmpty()
            binding.textCategory.text = sourceLabel(news.sourceType)
            binding.textCategory.visibility = if (news.sourceType.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.textDate.text = formatDate(news.publishedAt)
            binding.textAuthor.visibility = View.GONE
            binding.textViews.visibility = View.GONE
            binding.imageNews.visibility = View.GONE

            binding.toolbar.title = sourceLabel(news.sourceType).ifBlank { "Новость" }

            val sourceUrl = news.sourceUrl
            if (!sourceUrl.isNullOrBlank()) {
                binding.toolbar.menu.clear()
                binding.toolbar.inflateMenu(com.gorod.moygorodok.R.menu.menu_news_detail)
                binding.toolbar.setOnMenuItemClickListener { item ->
                    if (item.itemId == com.gorod.moygorodok.R.id.action_open_source) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl)))
                        true
                    } else {
                        false
                    }
                }
            } else {
                binding.toolbar.menu.clear()
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

    private fun sourceLabel(source: String?): String = when (source) {
        "manual" -> "От редакции"
        "rss" -> "RSS"
        else -> source.orEmpty()
    }

    private fun formatDate(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        return try {
            isoParser.parse(iso)?.let(displayFormat::format) ?: iso
        } catch (e: Exception) {
            iso
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
