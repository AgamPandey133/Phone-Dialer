package com.smartdialer.app.service

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import com.smartdialer.app.presentation.CallActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Handles incoming and outgoing calls provided by the Android Telecom system.
 * Acts as the bridge between the OS phone state and our app's UI.
 */
@AndroidEntryPoint
class SmartDialerCallService : InCallService() {

    @Inject
    lateinit var callManager: CallManager

    // Call callback to listen for state changes (Ringing -> Active -> Disconnected)
    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            callManager.updateCall(call)
            
            if (state == Call.STATE_DISCONNECTED) {
                // Delay slightly or handle cleanup
                callManager.updateCall(null)
            }
        }

        override fun onDetailsChanged(call: Call, details: Call.Details) {
            super.onDetailsChanged(call, details)
            callManager.updateCall(call)
        }
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        
        // Register for state updates on this specific call
        call.registerCallback(callCallback)
        
        // Update our manager
        callManager.updateCall(call)

        // Launch our custom Call UI (must use NEW_TASK flag from a Service)
        val intent = Intent(this, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        startActivity(intent)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        call.unregisterCallback(callCallback)
        if (call.state == Call.STATE_DISCONNECTED) {
            callManager.updateCall(null)
        }
    }
}
