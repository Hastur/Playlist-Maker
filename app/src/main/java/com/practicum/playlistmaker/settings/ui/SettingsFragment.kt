package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            nightModeSwitch.setOnCheckedChangeListener { _, checked ->
                viewModel.switchTheme(checked)
            }

            settingsShare.setOnClickListener {
                viewModel.shareApp()
            }

            settingsSupport.setOnClickListener {
                viewModel.sendMail(
                    requireActivity().getString(R.string.send_mail_address),
                    requireActivity().getString(R.string.send_mail_subject),
                    requireActivity().getString(R.string.send_mail_text)
                )
            }

            settingsUserAgreement.setOnClickListener {
                viewModel.openUserAgreement()
            }
        }

        viewModel.getDarkThemeEnabledLiveData().observe(viewLifecycleOwner) { isEnabled ->
            binding.nightModeSwitch.isChecked = isEnabled
        }
    }
}