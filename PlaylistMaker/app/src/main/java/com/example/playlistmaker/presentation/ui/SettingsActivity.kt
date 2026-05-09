package com.example.playlistmaker.presentation.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.ThemeInteractor
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var darkModeSwitch: Switch
    private lateinit var shareTheAppButton: Button
    private lateinit var writeToSupportButton: Button
    private lateinit var userAgreementButton: Button
    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        themeInteractor = Creator.provideThemeInteractor(this)
        setListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_button)
        darkModeSwitch = findViewById(R.id.dark_mode_switch)
        shareTheAppButton = findViewById(R.id.share_the_app_button)
        writeToSupportButton = findViewById(R.id.write_to_support_button)
        userAgreementButton = findViewById(R.id.user_agreement_button)
    }

    private fun setListeners() {
        backButton.setOnClickListener {
            finish()
        }

        darkModeSwitch.setOnCheckedChangeListener { switcher, _ ->
            val enabled = themeInteractor.isDarkModeEnabled()
            themeInteractor.setDarkModeEnabled(!enabled)
        }

        shareTheAppButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.url_to_the_course))
            }
            startActivity(Intent.createChooser(intent, null))
        }

        writeToSupportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_to_support_message))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_to_support_message))
            }

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    getString(R.string.toast_no_email_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        userAgreementButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = getString(R.string.url_to_the_offer).toUri()
            }
            startActivity(intent)
        }
    }
}
