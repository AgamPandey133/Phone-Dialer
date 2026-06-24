package com.smartdialer.app.presentation.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartdialer.app.domain.model.Contact
import com.smartdialer.app.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val contacts: StateFlow<List<Contact>> = combine(
        contactRepository.getAllContacts(),
        _searchQuery
    ) { allContacts, query ->
        if (query.isBlank()) {
            allContacts
        } else {
            allContacts.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.phoneNumbers.any { phone -> phone.number.contains(query) }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    fun syncContacts() {
        viewModelScope.launch {
            contactRepository.syncFromDevice()
        }
    }
}
