package com.smartdialer.app.service

import android.telecom.Call
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton manager to hold the active Android Telecom Call object
 * and expose its state as a Flow to the UI layer (CallViewModel).
 */
@Singleton
class CallManager @Inject constructor() {

    private var activeCall: Call? = null

    private val _callState = MutableStateFlow(Call.STATE_DISCONNECTED)
    val callState: StateFlow<Int> = _callState.asStateFlow()

    private val _callDetails = MutableStateFlow<Call.Details?>(null)
    val callDetails: StateFlow<Call.Details?> = _callDetails.asStateFlow()

    fun updateCall(call: Call?) {
        this.activeCall = call
        if (call != null) {
            _callState.value = call.state
            _callDetails.value = call.details
        } else {
            _callState.value = Call.STATE_DISCONNECTED
            _callDetails.value = null
        }
    }

    fun answerCall() {
        activeCall?.answer(0)
    }

    fun rejectCall() {
        if (activeCall?.state == Call.STATE_RINGING) {
            activeCall?.reject(false, null)
        }
    }

    fun disconnectCall() {
        activeCall?.disconnect()
    }

    fun playDtmfTone(char: Char) {
        activeCall?.playDtmfTone(char)
        activeCall?.stopDtmfTone() // Short pulse
    }

    fun setMuted(muted: Boolean) {
        // Mute state is usually handled via InCallService.setMuted, 
        // but we'll manage it via CallService directly or pass an event.
    }
}
