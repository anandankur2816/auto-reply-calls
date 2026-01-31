package com.autoreply.calls.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager class for handling SharedPreferences storage.
 * Stores user preferences for status, custom message, and auto-reply settings.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "auto_reply_prefs"
        private const val KEY_CURRENT_STATUS = "current_status"
        private const val KEY_CUSTOM_MESSAGE = "custom_message"
        private const val KEY_AUTO_REPLY_ENABLED = "auto_reply_enabled"
        private const val KEY_LAST_REPLIED_NUMBER = "last_replied_number"
        private const val KEY_LAST_REPLY_TIMESTAMP = "last_reply_timestamp"
        
        // Spam prevention: minimum time between SMS to same number (5 minutes)
        const val SPAM_PREVENTION_DELAY_MS = 5 * 60 * 1000L
    }

    var currentStatus: StatusType
        get() {
            val statusName = prefs.getString(KEY_CURRENT_STATUS, StatusType.BUSY.name)
            return try {
                StatusType.valueOf(statusName ?: StatusType.BUSY.name)
            } catch (e: Exception) {
                StatusType.BUSY
            }
        }
        set(value) {
            prefs.edit().putString(KEY_CURRENT_STATUS, value.name).apply()
        }

    var customMessage: String
        get() = prefs.getString(KEY_CUSTOM_MESSAGE, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_CUSTOM_MESSAGE, value).apply()
        }

    var isAutoReplyEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_REPLY_ENABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_AUTO_REPLY_ENABLED, value).apply()
        }

    private var lastRepliedNumber: String
        get() = prefs.getString(KEY_LAST_REPLIED_NUMBER, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_LAST_REPLIED_NUMBER, value).apply()
        }

    private var lastReplyTimestamp: Long
        get() = prefs.getLong(KEY_LAST_REPLY_TIMESTAMP, 0L)
        set(value) {
            prefs.edit().putLong(KEY_LAST_REPLY_TIMESTAMP, value).apply()
        }

    /**
     * Get the current message to send based on status.
     * Returns custom message if status is CUSTOM, otherwise returns the default status message.
     */
    fun getCurrentMessage(): String {
        return if (currentStatus == StatusType.CUSTOM) {
            customMessage.ifEmpty { "I missed your call. Will get back to you soon!" }
        } else {
            currentStatus.defaultMessage
        }
    }

    /**
     * Check if we should send SMS to this number (spam prevention).
     * Returns true if we haven't sent to this number recently.
     */
    fun shouldSendSms(phoneNumber: String): Boolean {
        val normalizedNumber = normalizePhoneNumber(phoneNumber)
        val currentTime = System.currentTimeMillis()
        
        if (lastRepliedNumber == normalizedNumber) {
            val timeSinceLastReply = currentTime - lastReplyTimestamp
            if (timeSinceLastReply < SPAM_PREVENTION_DELAY_MS) {
                return false
            }
        }
        return true
    }

    /**
     * Record that we sent an SMS to this number.
     */
    fun recordSmsSent(phoneNumber: String) {
        lastRepliedNumber = normalizePhoneNumber(phoneNumber)
        lastReplyTimestamp = System.currentTimeMillis()
    }

    /**
     * Normalize phone number by removing non-digit characters.
     */
    private fun normalizePhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace(Regex("[^0-9+]"), "")
    }
}
