package com.smartdialer.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartdialer.app.domain.repository.CallLogRepository
import com.smartdialer.app.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val callLogRepository: CallLogRepository
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private var hasSynced = false

    fun initialSync() {
        if (hasSynced) return
        
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                contactRepository.syncFromDevice()
                callLogRepository.syncFromDevice()
                hasSynced = true
            } catch (e: Exception) {
                // In a real app, we'd log this or show an error state
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
