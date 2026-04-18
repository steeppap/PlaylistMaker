package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.UiModeManager.MODE_NIGHT_YES
import android.app.UiModeManager.MODE_NIGHT_NO
import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var darkModeSwitch: Switch
    private lateinit var shareTheAppButton: Button
    private lateinit var writeToSupportButton: Button
    private lateinit var userAgreementButton: Button


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

        darkModeSwitch.setOnCheckedChangeListener { switcher, сhecked ->
            (applicationContext as App).switchTheme(сhecked)
        }

        shareTheAppButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.url_to_the_course))
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, null))
        }

        writeToSupportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:${getString(R.string.support_email)}")
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_to_support_message))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_to_support_message))
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
            val intent = Intent(android.content.Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.url_to_the_offer))
            startActivity(intent)
        }
    }
}
