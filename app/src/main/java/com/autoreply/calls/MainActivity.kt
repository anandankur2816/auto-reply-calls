package com.autoreply.calls

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.autoreply.calls.data.PreferencesManager
import com.autoreply.calls.data.StatusType
import com.autoreply.calls.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.SEND_SMS
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                "Permissions required for auto-reply to work",
                Toast.LENGTH_LONG
            ).show()
            // Disable auto-reply if permissions not granted
            preferencesManager.isAutoReplyEnabled = false
            binding.switchAutoReply.isChecked = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)

        setupUI()
        loadSavedState()
    }

    private fun setupUI() {
        // Setup status cards click listeners
        binding.cardLunch.setOnClickListener { selectStatus(StatusType.LUNCH) }
        binding.cardMeeting.setOnClickListener { selectStatus(StatusType.MEETING) }
        binding.cardDriving.setOnClickListener { selectStatus(StatusType.DRIVING) }
        binding.cardBusy.setOnClickListener { selectStatus(StatusType.BUSY) }
        binding.cardCustom.setOnClickListener { selectStatus(StatusType.CUSTOM) }

        // Setup auto-reply toggle
        binding.switchAutoReply.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !hasAllPermissions()) {
                requestPermissions()
                return@setOnCheckedChangeListener
            }
            preferencesManager.isAutoReplyEnabled = isChecked
            updateStatusIndicator()
        }

        // Setup custom message input
        binding.editCustomMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                preferencesManager.customMessage = s?.toString() ?: ""
            }
        })
    }

    private fun loadSavedState() {
        // Load saved status
        selectStatus(preferencesManager.currentStatus)
        
        // Load custom message
        binding.editCustomMessage.setText(preferencesManager.customMessage)
        
        // Load auto-reply state
        binding.switchAutoReply.isChecked = preferencesManager.isAutoReplyEnabled
        
        updateStatusIndicator()
    }

    private fun selectStatus(status: StatusType) {
        preferencesManager.currentStatus = status

        // Reset all cards
        resetAllCards()

        // Highlight selected card
        val selectedCard = when (status) {
            StatusType.LUNCH -> binding.cardLunch
            StatusType.MEETING -> binding.cardMeeting
            StatusType.DRIVING -> binding.cardDriving
            StatusType.BUSY -> binding.cardBusy
            StatusType.CUSTOM -> binding.cardCustom
        }
        selectedCard.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.selected_card_background)
        )

        // Show/hide custom message input
        binding.layoutCustomMessage.visibility = 
            if (status == StatusType.CUSTOM) View.VISIBLE else View.GONE

        updateStatusIndicator()
    }

    private fun resetAllCards() {
        val defaultColor = ContextCompat.getColor(this, R.color.card_background)
        binding.cardLunch.setCardBackgroundColor(defaultColor)
        binding.cardMeeting.setCardBackgroundColor(defaultColor)
        binding.cardDriving.setCardBackgroundColor(defaultColor)
        binding.cardBusy.setCardBackgroundColor(defaultColor)
        binding.cardCustom.setCardBackgroundColor(defaultColor)
    }

    private fun updateStatusIndicator() {
        val status = preferencesManager.currentStatus
        val isEnabled = preferencesManager.isAutoReplyEnabled

        if (isEnabled) {
            binding.textStatusIndicator.text = "Auto-reply ON: ${status.getFullDisplayName()}"
            binding.textStatusIndicator.setTextColor(
                ContextCompat.getColor(this, R.color.status_active)
            )
        } else {
            binding.textStatusIndicator.text = "Auto-reply is OFF"
            binding.textStatusIndicator.setTextColor(
                ContextCompat.getColor(this, R.color.status_inactive)
            )
        }
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        permissionLauncher.launch(requiredPermissions)
    }
}
