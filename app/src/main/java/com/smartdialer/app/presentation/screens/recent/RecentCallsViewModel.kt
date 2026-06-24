package com.smartdialer.app.presentation.screens.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartdialer.app.domain.model.GroupedCallLog
import com.smartdialer.app.domain.repository.CallLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentCallsViewModel @Inject constructor(
    private val callLogRepository: CallLogRepository
) : ViewModel() {

    val recentCalls: StateFlow<List<GroupedCallLog>> = callLogRepository.getGroupedCallLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun syncCallLogs() {
        viewModelScope.launch {
            callLogRepository.syncFromDevice()
        }
    }
}
