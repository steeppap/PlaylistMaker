package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.example.playlistmaker.settings.ui.view_model.ToastState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()
    
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
        
        viewModel.observeDarkModeState().observe(this) { isDark ->
            binding.darkModeSwitch.isChecked = isDark
        }
        
        viewModel.observeToastState().observe(this) { state ->
            when (state) {
                
                is ToastState.ShowToast -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    
                    viewModel.hideToast()
                }
                
                ToastState.HideToast -> return@observe
            }
        }
        
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
