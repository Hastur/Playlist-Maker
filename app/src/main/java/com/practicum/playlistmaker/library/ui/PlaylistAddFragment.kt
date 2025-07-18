package com.practicum.playlistmaker.library.ui

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistAddBinding
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class PlaylistAddFragment : Fragment() {

    companion object {
        private const val DIRECTORY_NAME = "playlists"
        private const val WITH_FRAGMENT_MANAGER = "WITH_FRAGMENT_MANAGER"

        fun newInstance(withFragmentManager: Boolean): PlaylistAddFragment =
            PlaylistAddFragment().apply {
                arguments = bundleOf(WITH_FRAGMENT_MANAGER to withFragmentManager)
            }
    }

    private var _binding: FragmentPlaylistAddBinding? = null
    private val binding: FragmentPlaylistAddBinding get() = _binding!!

    private val viewModel by viewModel<PlaylistsViewModel>()

    private var coverUri: Uri? = null
    private lateinit var confirmDialog: MaterialAlertDialogBuilder

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

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.run {
                    if (s.isNullOrEmpty()) {
                        buttonCreate.run {
                            isEnabled = false
                            backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireActivity(), R.color.grey)
                            )
                        }
                    } else {
                        buttonCreate.run {
                            isEnabled = true
                            backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(requireActivity(), R.color.blue)
                            )
                            setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        binding.run {
            toolbarAddPlaylist.setNavigationOnClickListener {
                checkIsEditing()
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
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            inputName.editText?.addTextChangedListener(textWatcher)

            buttonCreate.setOnClickListener {
                val storedCoverUri = if (coverUri != null) saveCoverToStorage()
                else ""

                viewModel.addPlaylist(
                    name = binding.inputName.editText?.text.toString(),
                    description = binding.inputDescription.editText?.text.toString(),
                    coverPath = storedCoverUri.toString()
                )
            }
        }

        viewModel.getAddPlaylistSingle().observe(viewLifecycleOwner) { isAdded ->
            if (isAdded) {
                val text = getString(R.string.playlist_finished, binding.inputName.editText?.text)
                Toast.makeText(requireActivity(), text, Toast.LENGTH_SHORT).show()
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    checkIsEditing()
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

    private fun closeFragment() {
        if (arguments?.getBoolean(WITH_FRAGMENT_MANAGER) == true) parentFragmentManager.popBackStack()
        else findNavController().popBackStack()
    }
}