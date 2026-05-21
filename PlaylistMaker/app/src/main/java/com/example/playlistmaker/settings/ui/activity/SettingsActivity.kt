package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharingInteractor = Creator.provideSharingInteractor(this)
        val themeInteractor = Creator.provideThemeInteractor(this)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(sharingInteractor, themeInteractor)
        )[SettingsViewModel::class.java]

        setListeners()
    }

    private fun setListeners() {
        
        binding.apply {
            backButton.setOnClickListener {
                finish()
            }

            darkModeSwitch.setOnCheckedChangeListener { switcher, _ ->
                viewModel.setNecessaryTheme()
            }

            shareTheAppButton.setOnClickListener {
                viewModel.shareApp()
            }

            userAgreementButton.setOnClickListener {
                viewModel.openUserAgreement()
            }

            writeToSupportButton.setOnClickListener {
                viewModel.writeToSupport()
            }
        }
    }
}
