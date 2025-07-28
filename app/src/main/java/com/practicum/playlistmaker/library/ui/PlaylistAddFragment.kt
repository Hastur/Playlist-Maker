package com.practicum.playlistmaker.library.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistAddBinding
import com.practicum.playlistmaker.library.domain.models.PlaylistInfo
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.util.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class PlaylistAddFragment : Fragment() {

    companion object {
        private const val DIRECTORY_NAME = "playlists"
        private const val WITH_FRAGMENT_MANAGER = "WITH_FRAGMENT_MANAGER"
        private const val PLAYLIST_INFO = "PLAYLIST_INFO"

        fun newInstance(withFragmentManager: Boolean): PlaylistAddFragment =
            PlaylistAddFragment().apply {
                arguments = bundleOf(WITH_FRAGMENT_MANAGER to withFragmentManager)
            }

        fun createArgs(playlistInfo: String): Bundle = bundleOf(PLAYLIST_INFO to playlistInfo)
    }

    private var _binding: FragmentPlaylistAddBinding? = null
    private val binding: FragmentPlaylistAddBinding get() = _binding!!

    private val viewModel by viewModel<PlaylistsViewModel>()

    private var coverUri: Uri? = null
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private lateinit var permissionDialog: MaterialAlertDialogBuilder
    private var playlistInfo: PlaylistInfo? = null
    private var inUpdateMode = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                permissionDialog.show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            val serializedData = arguments?.getString(PLAYLIST_INFO)
            if (serializedData != null) {
                inUpdateMode = true
                playlistInfo = Utils().createFromJson(serializedData, PlaylistInfo::class.java)
                binding.run {
                    coverUri = playlistInfo?.coverPath?.toUri()
                    playlistCover.run {
                        if (coverUri != "".toUri()) setImageURI(coverUri)
                        else setImageResource(R.drawable.ic_track_placeholder)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    inputName.editText?.setText(playlistInfo?.title)
                    inputDescription.editText?.setText(playlistInfo?.description ?: "")
                    buttonCreate.run {
                        setText(R.string.playlist_edit_save)
                        toggleButton(playlistInfo?.title?.isEmpty() ?: true)
                    }
                }
            }

            toolbarAddPlaylist.setNavigationOnClickListener {
                if (inUpdateMode) closeFragment()
                else checkIsEditing()
            }

            fun checkPermission(permission: String): Boolean {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    return true
                } else {
                    requestPermissionLauncher.launch(permission)
                    return false
                }
            }

            val pickMedia =
                registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    if (uri != null) {
                        playlistCover.setImageURI(uri)
                        playlistCover.scaleType = ImageView.ScaleType.CENTER_CROP
                        coverUri = uri
                    }
                }

            playlistCover.setOnClickListener {
                val permission =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
                    else Manifest.permission.READ_EXTERNAL_STORAGE

                if (checkPermission(permission)) {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }

            inputName.editText?.doOnTextChanged { text, _, _, _ ->
                binding.run {
                    toggleButton(text?.trim().isNullOrEmpty())
                }
            }

            buttonCreate.setOnClickListener {
                val storedCoverUri = if (coverUri != null) saveCoverToStorage()
                else ""

                if (inUpdateMode) {
                    viewModel.editPlaylist(
                        name = binding.inputName.editText?.text.toString().trim(),
                        description = binding.inputDescription.editText?.text.toString().trim(),
                        coverPath = storedCoverUri.toString(),
                        playlistInfo = playlistInfo
                    )
                } else {
                    viewModel.addPlaylist(
                        name = binding.inputName.editText?.text.toString().trim(),
                        description = binding.inputDescription.editText?.text.toString().trim(),
                        coverPath = storedCoverUri.toString()
                    )
                }
            }
        }

        viewModel.getPlaylistChangedSingle().observe(viewLifecycleOwner) { isChanged ->
            if (isChanged) {
                if (!inUpdateMode) {
                    val text =
                        getString(R.string.playlist_finished, binding.inputName.editText?.text)
                    Toast.makeText(requireActivity(), text, Toast.LENGTH_SHORT).show()
                }
                closeFragment()
            }
        }

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.playlist_dialog_title)
            .setMessage(R.string.playlist_dialog_message)
            .setNeutralButton(R.string.playlist_dialog_cancel) { _, _ ->
            }
            .setPositiveButton(R.string.playlist_dialog_finish) { _, _ ->
                closeFragment()
            }

        permissionDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.permission_dialog_title)
            .setMessage(R.string.permission_dialog_message)
            .setNeutralButton(R.string.playlist_dialog_cancel) { _, _ ->
            }
            .setPositiveButton(R.string.permission_dialog_settings) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.fromParts("package", context?.packageName, null)
                context?.startActivity(intent)
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (inUpdateMode) closeFragment()
                    else checkIsEditing()
                }
            })
    }

    private fun saveCoverToStorage(): Uri {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            DIRECTORY_NAME
        )
        if (!filePath.exists()) filePath.mkdirs()
        val fileName = "cover_" + coverUri.toString().substringAfterLast("/")
        val file = File(filePath, "$fileName.jpg")

        val inputStream = coverUri?.let { requireActivity().contentResolver.openInputStream(it) }
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toUri()
    }

    private fun checkIsEditing() {
        if (coverUri != null
            || !binding.inputName.editText?.text.isNullOrEmpty()
            || !binding.inputDescription.editText?.text.isNullOrEmpty()
        ) confirmDialog.show()
        else closeFragment()
    }

    private fun toggleButton(emptyText: Boolean) {
        binding.buttonCreate.run {
            isEnabled = !emptyText
            if (emptyText) {
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireActivity(), R.color.grey)
                )
            } else {
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(requireActivity(), R.color.blue)
                )
                setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
            }
        }
    }

    private fun closeFragment() {
        if (arguments?.getBoolean(WITH_FRAGMENT_MANAGER) == true) parentFragmentManager.popBackStack()
        else findNavController().popBackStack()
    }
}