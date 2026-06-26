package com.smartdialer.app.presentation.screens.keypad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartdialer.app.domain.model.Contact
import com.smartdialer.app.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class KeypadViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _dialNumber = MutableStateFlow("")

    val t9Results: StateFlow<List<Contact>> = _dialNumber
        .debounce(150) // Small debounce so we don't search on every keystroke
        .flatMapLatest { query ->
            if (query.isBlank() || query.length < 2) {
                flowOf(emptyList())
            } else {
                contactRepository.searchByT9(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onDialNumberChanged(number: String) {
        _dialNumber.value = number
    }
}
