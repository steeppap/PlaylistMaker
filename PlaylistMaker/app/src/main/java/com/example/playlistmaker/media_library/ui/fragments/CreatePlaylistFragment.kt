package com.example.playlistmaker.media_library.ui.fragments

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media_library.ui.viewmodels.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class CreatePlaylistFragment : Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePlaylistViewModel by viewModel()
    private lateinit var titleTextWatcher: TextWatcher
    private lateinit var descriptionTextWatcher: TextWatcher
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    val requester = PermissionRequester.instance()
    private var isEditMode = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
        setListeners()
    }
    
    override fun onDestroyView() {
        binding.titleEditText.removeTextChangedListener(titleTextWatcher)
        binding.descriptionEditText.removeTextChangedListener(descriptionTextWatcher)
        _binding = null
        super.onDestroyView()
    }
    
    private fun initFragment() {
        viewModel.observeTitleEditText().observe(viewLifecycleOwner) { currentText ->
            binding.createButton.isEnabled = currentText.isNotBlank()
            
            val currentEditText = binding.titleEditText.text.toString()
            if (currentEditText != currentText) {
                binding.titleEditText.setText(currentText)
            }
        }
        
        viewModel.observeDescEditText().observe(viewLifecycleOwner) { currentText ->
            val currentEditText = binding.descriptionEditText.text.toString()
            if (currentEditText != currentText) {
                binding.descriptionEditText.setText(currentText)
            }
        }
        
        viewModel.observeCoverPath().observe(viewLifecycleOwner) { coverPath ->
            Glide.with(binding.playlistPhoto)
                .load(coverPath)
                .fitCenter()
                .into(binding.playlistPhoto)
        }
        
        viewModel.observeIsEditMode().observe(viewLifecycleOwner) {
            if (it) {
                isEditMode = it
                binding.fragmentTitle.setText(R.string.edit)
                binding.createButton.setText(R.string.save)
            }
        }
        
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                
                if (uri != null) {
                    binding.playlistPhoto.setImageURI(uri)
                    saveImageToPrivateStorage(uri)
                    
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
    }
    
    private fun setListeners() {
        binding.apply {
            backButton.setOnClickListener {
                if (isEditMode) {
                    findNavController().navigateUp()
                } else showExitDialog()
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (isEditMode) {
                    findNavController().navigateUp()
                } else showExitDialog()
            }
            createButton.setOnClickListener {
                if (isEditMode) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.savePlaylistInfo()
                        withContext(Dispatchers.Main) {
                            findNavController().navigateUp()
                        }
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.createPlaylist()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                getString(
                                    R.string.playlist_has_been_created,
                                    titleEditText.text.trim()
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigateUp()
                        }
                    }
                }
            }
            titleTextWatcher = titleEditText.doOnTextChanged { s, _, _, _ ->
                val newText = s.toString()
                viewModel.updateTitleEditText(newText)
            }
            
            descriptionTextWatcher = descriptionEditText.doOnTextChanged { s, _, _, _ ->
                val newText = s.toString()
                viewModel.updateDescEditText(newText)
            }
            
            addPhoto.setOnClickListener {
                lifecycleScope.launch {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requester.request(Manifest.permission.READ_MEDIA_IMAGES).collect { result ->
                            when (result) {
                                is PermissionResult.Granted -> {
                                    pickMedia.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                
                                is PermissionResult.Denied -> {
                                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                                        
                                        Toast.makeText(
                                            requireContext(),
                                            R.string.permission_to_select_playlist_cover,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        
                                    } else {
                                        val intent =
                                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        intent.data = Uri.fromParts(
                                            "package",
                                            requireContext().packageName,
                                            null
                                        )
                                        requireContext().startActivity(intent)
                                    }
                                }
                                
                                is PermissionResult.Cancelled -> {}
                            }
                        }
                    }
                }
            }
        }
    }
    
    private fun saveImageToPrivateStorage(uri: Uri) {
        val dir = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "covers"
        )
        if (!dir.exists()) {
            dir.mkdirs()
        }
        
        val file = File(dir, "cover_${System.currentTimeMillis()}.jpg")
        
        var rotationDegrees = 0
        
        requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
            try {
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                
                rotationDegrees = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            } catch (e: Exception) {
                rotationDegrees = 0
            }
        }
        
        requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
            try {
                val bitmap = BitmapFactory.decodeStream(inputStream)
                
                if (bitmap != null) {
                    val finalBitmap = if (rotationDegrees != 0) {
                        val matrix = Matrix()
                        matrix.postRotate(rotationDegrees.toFloat())
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    } else {
                        bitmap
                    }
                    
                    file.outputStream().use { outputStream ->
                        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                    }
                    
                    viewModel.setCoverPath(file.absolutePath)
                    
                    Log.d("SavePhoto", "Фото сохранено: ${file.absolutePath}")
                    
                    if (finalBitmap !== bitmap) bitmap.recycle()
                    finalBitmap.recycle()
                } else {
                    Log.e("SavePhoto", "Не удалось декодировать Bitmap (null)")
                }
            } catch (e: Exception) {
                Log.e("SavePhoto", "Ошибка при декодировании или сохранении: ${e.message}")
            }
        }
    }
    
    private fun showExitDialog() {
        if (binding.titleEditText.text.isNotEmpty()
            || binding.descriptionEditText.text.isNotEmpty()
            || binding.playlistPhoto.drawable != null
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.finish_creating_the_playlist)
                .setMessage(R.string.all_unsaved_data_will_be_lost)
                .setNeutralButton(R.string.cancel) { dialog, which ->
                
                }
                .setPositiveButton(R.string.finish) { dialog, which ->
                    findNavController().navigateUp()
                }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }
    
    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"
        
        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
}
