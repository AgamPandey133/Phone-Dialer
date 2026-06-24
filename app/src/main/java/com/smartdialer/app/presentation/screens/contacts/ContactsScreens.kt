package com.smartdialer.app.presentation.screens.contacts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ContactsScreen(
    onNavigateToContactDetail: (Long) -> Unit,
    onNavigateToAddContact: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Contacts Screen")
    }
}

@Composable
fun ContactDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Contact Detail Screen")
    }
}

@Composable
fun AddEditContactScreen(
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Add/Edit Contact Screen")
    }
}
