package com.gorod.moygorodok.ui.avatar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gorod.moygorodok.AuthActivity
import com.gorod.moygorodok.databinding.FragmentAvatarBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class AvatarFragment : Fragment() {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AvatarViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.setSelectedImage(uri)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Snackbar.make(
                binding.root,
                "Требуется разрешение для доступа к галерее",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.buttonSelectImage.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        binding.imageAvatar.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        binding.buttonSave.setOnClickListener {
            viewModel.saveAvatar()
        }

        binding.buttonRemove.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить аватар")
                .setMessage("Вы уверены, что хотите удалить аватар?")
                .setPositiveButton("Удалить") { _, _ ->
                    viewModel.removeAvatar()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }

        binding.buttonSkip.setOnClickListener {
            navigateNext()
        }
    }

    private fun checkPermissionAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Snackbar.make(
                    binding.root,
                    "Требуется разрешение для выбора изображения",
                    Snackbar.LENGTH_LONG
                ).setAction("Разрешить") {
                    requestPermissionLauncher.launch(permission)
                }.show()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun observeViewModel() {
        viewModel.selectedImageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                binding.imageAvatar.setImageURI(uri)
                binding.buttonSave.isEnabled = true
                binding.buttonRemove.visibility = View.VISIBLE
            } else {
                binding.imageAvatar.setImageResource(com.gorod.moygorodok.R.drawable.ic_add_photo)
                binding.buttonSave.isEnabled = false
                binding.buttonRemove.visibility = View.GONE
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                if (!it.avatarUrl.isNullOrEmpty()) {
                    try {
                        binding.imageAvatar.setImageURI(Uri.parse(it.avatarUrl))
                        binding.buttonRemove.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        // Ignore invalid URI
                    }
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setLoading(isLoading)
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Аватар сохранен", Snackbar.LENGTH_SHORT).show()
                viewModel.resetSaveSuccess()
                navigateNext()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun navigateNext() {
        // После регистрации переходим в главное приложение
        (activity as? AuthActivity)?.navigateToMain()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSave.isEnabled = !isLoading && viewModel.selectedImageUri.value != null
        binding.buttonSelectImage.isEnabled = !isLoading
        binding.buttonRemove.isEnabled = !isLoading
        binding.buttonSkip.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
