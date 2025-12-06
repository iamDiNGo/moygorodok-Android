package com.gorod.moygorodok.ui.ads

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gorod.moygorodok.data.model.PriceType
import com.gorod.moygorodok.databinding.FragmentAdDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class AdDetailFragment : Fragment() {

    private var _binding: FragmentAdDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdDetailViewModel by viewModels()

    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
    private val priceFormat = NumberFormat.getNumberInstance(Locale("ru"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        observeViewModel()

        val adId = arguments?.getString("adId")
        if (adId != null) {
            viewModel.loadAd(adId)
        } else {
            Snackbar.make(binding.root, "Ошибка: ID объявления не найден", Snackbar.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                com.gorod.moygorodok.R.id.action_favorite -> {
                    viewModel.toggleFavorite()
                    true
                }
                com.gorod.moygorodok.R.id.action_share -> {
                    shareAd()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.ad.observe(viewLifecycleOwner) { ad ->
            ad?.let {
                // Цена
                binding.textPrice.text = when (it.priceType) {
                    PriceType.FREE -> "Бесплатно"
                    PriceType.EXCHANGE -> "Обмен"
                    PriceType.NEGOTIABLE -> "${priceFormat.format(it.price)} ₽"
                    PriceType.FIXED -> "${priceFormat.format(it.price)} ₽"
                }

                if (it.priceType == PriceType.NEGOTIABLE) {
                    binding.textPriceType.visibility = View.VISIBLE
                    binding.textPriceType.text = "Торг уместен"
                } else {
                    binding.textPriceType.visibility = View.GONE
                }

                binding.textTitle.text = it.title
                binding.textDescription.text = it.description
                binding.textCategory.text = it.category.displayName
                binding.textLocation.text = it.location
                binding.textDate.text = "Опубликовано ${dateFormat.format(it.createdAt)}"
                binding.textViews.text = "${it.viewCount} просмотров"

                // Информация о продавце
                binding.textSellerName.text = it.seller.name
                binding.textSellerRating.text = "★ ${it.seller.rating}"
                binding.textSellerAdsCount.text = "${it.seller.adsCount} объявлений"

                // Кнопки действий
                binding.buttonCall.setOnClickListener { _ ->
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${it.seller.phone}")
                    }
                    startActivity(intent)
                }

                binding.buttonMessage.setOnClickListener { _ ->
                    // В реальном приложении здесь был бы переход в чат
                    Snackbar.make(binding.root, "Функция сообщений в разработке", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.toolbar.menu.findItem(com.gorod.moygorodok.R.id.action_favorite)?.let { menuItem ->
                menuItem.setIcon(
                    if (isFavorite) com.gorod.moygorodok.R.drawable.ic_favorite_filled
                    else com.gorod.moygorodok.R.drawable.ic_favorite_outline
                )
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

    private fun shareAd() {
        val ad = viewModel.ad.value ?: return

        val shareText = """
            ${ad.title}

            Цена: ${when (ad.priceType) {
                PriceType.FREE -> "Бесплатно"
                PriceType.EXCHANGE -> "Обмен"
                else -> "${priceFormat.format(ad.price)} ₽"
            }}

            ${ad.location}

            Отправлено из приложения MoyGorodok
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Поделиться объявлением"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
