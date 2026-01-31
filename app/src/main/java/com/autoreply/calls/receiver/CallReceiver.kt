package com.autoreply.calls.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.autoreply.calls.sms.SmsSender

/**
 * BroadcastReceiver that listens for phone call state changes.
 * Detects missed calls and triggers auto-reply SMS.
 */
class CallReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "CallReceiver"
        
        // Static variables to track call state across broadcasts
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var incomingNumber: String? = null
        private var isIncoming = false
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            return
        }

        val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        
        val state = when (stateStr) {
            TelephonyManager.EXTRA_STATE_RINGING -> TelephonyManager.CALL_STATE_RINGING
            TelephonyManager.EXTRA_STATE_OFFHOOK -> TelephonyManager.CALL_STATE_OFFHOOK
            TelephonyManager.EXTRA_STATE_IDLE -> TelephonyManager.CALL_STATE_IDLE
            else -> return
        }

        onCallStateChanged(context, state, number)
    }

    private fun onCallStateChanged(context: Context, state: Int, number: String?) {
        // Save the incoming number when ringing starts
        if (state == TelephonyManager.CALL_STATE_RINGING && number != null) {
            incomingNumber = number
            isIncoming = true
            Log.d(TAG, "Incoming call from: $number")
        }

        // Detect missed call: RINGING -> IDLE (without going to OFFHOOK)
        if (lastState == TelephonyManager.CALL_STATE_RINGING && 
            state == TelephonyManager.CALL_STATE_IDLE && 
            isIncoming) {
            
            // This is a missed call!
            Log.i(TAG, "Missed call detected from: $incomingNumber")
            
            incomingNumber?.let { phoneNumber ->
                val smsSender = SmsSender(context)
                smsSender.sendAutoReplyMessage(phoneNumber)
            }
        }

        // Reset tracking when call ends
        if (state == TelephonyManager.CALL_STATE_IDLE) {
            isIncoming = false
            incomingNumber = null
        }

        lastState = state
    }
}
