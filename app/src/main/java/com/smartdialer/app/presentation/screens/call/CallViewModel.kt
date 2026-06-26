package com.smartdialer.app.presentation.screens.call

import android.telecom.Call
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartdialer.app.domain.model.Contact
import com.smartdialer.app.domain.repository.ContactRepository
import com.smartdialer.app.service.CallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callManager: CallManager,
    private val contactRepository: ContactRepository
) : ViewModel() {

    val callState: StateFlow<Int> = callManager.callState

    // Map the telecom details to extract the phone number being called/calling
    private val phoneNumber: Flow<String?> = callManager.callDetails.map { details ->
        details?.handle?.schemeSpecificPart
    }

    // Try to find a matching contact in our local database
    val contact: StateFlow<Contact?> = phoneNumber.flatMapLatest { number ->
        if (number.isNullOrBlank()) {
            flowOf(null)
        } else {
            // Very naive search for demonstration.
            // In a real app, we'd search specifically by normalized phone number.
            contactRepository.searchContacts(number).map { list -> list.firstOrNull() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    val currentNumber: StateFlow<String> = phoneNumber.map { it ?: "Unknown Number" }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Unknown Number"
    )

    fun answerCall() {
        callManager.answerCall()
    }

    fun rejectCall() {
        callManager.rejectCall()
    }

    fun disconnectCall() {
        callManager.disconnectCall()
    }
}
