package com.example.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.example.playlistmaker.settings.ui.view_model.ToastState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()
    
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
        
        viewModel.observeToastState().observe(viewLifecycleOwner) { state ->
            when (state) {
                
                is ToastState.ShowToast -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    
                    viewModel.hideToast()
                }
                
                ToastState.HideToast -> return@observe
            }
        }
        
        setListeners()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setListeners() {
        
        binding.apply {
            
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
