package com.autoreply.calls.sms

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import com.autoreply.calls.data.PreferencesManager

/**
 * Utility class for sending SMS messages.
 */
class SmsSender(private val context: Context) {

    companion object {
        private const val TAG = "SmsSender"
        private const val MAX_SMS_LENGTH = 160
        private const val MESSAGE_SIGNATURE = "\n\n-Sent from Auto Reply Calls"
    }

    private val preferencesManager = PreferencesManager(context)

    /**
     * Send an SMS to the specified phone number with the current status message.
     * Includes spam prevention check.
     * 
     * @param phoneNumber The destination phone number
     * @return true if SMS was sent, false if blocked by spam prevention or error
     */
    fun sendAutoReplyMessage(phoneNumber: String): Boolean {
        // Check if auto-reply is enabled
        if (!preferencesManager.isAutoReplyEnabled) {
            Log.d(TAG, "Auto-reply is disabled, not sending SMS")
            return false
        }

        // Check spam prevention
        if (!preferencesManager.shouldSendSms(phoneNumber)) {
            Log.d(TAG, "Spam prevention: Already sent SMS to $phoneNumber recently")
            return false
        }

        val message = preferencesManager.getCurrentMessage() + MESSAGE_SIGNATURE
        
        return try {
            sendSms(phoneNumber, message)
            preferencesManager.recordSmsSent(phoneNumber)
            Log.i(TAG, "SMS sent successfully to $phoneNumber")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS: ${e.message}", e)
            false
        }
    }

    /**
     * Send SMS using SmsManager.
     * Handles message splitting for long messages.
     */
    private fun sendSms(phoneNumber: String, message: String) {
        val smsManager = context.getSystemService(SmsManager::class.java)
        
        if (message.length <= MAX_SMS_LENGTH) {
            smsManager.sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )
        } else {
            // Split long messages
            val parts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(
                phoneNumber,
                null,
                parts,
                null,
                null
            )
        }
    }
}
