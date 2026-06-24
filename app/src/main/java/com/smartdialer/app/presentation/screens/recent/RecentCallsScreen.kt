package com.smartdialer.app.presentation.screens.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartdialer.app.domain.model.CallType
import com.smartdialer.app.domain.model.GroupedCallLog
import com.smartdialer.app.presentation.theme.CallIncomingColor
import com.smartdialer.app.presentation.theme.CallMissedColor
import com.smartdialer.app.presentation.theme.CallOutgoingColor
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentCallsScreen(
    onNavigateToContactDetail: (Long) -> Unit,
    viewModel: RecentCallsViewModel = hiltViewModel()
) {
    val recentCalls by viewModel.recentCalls.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (recentCalls.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No recent calls",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(recentCalls) { groupedCall ->
                    RecentCallItem(
                        groupedCall = groupedCall,
                        onInfoClick = {
                            // If we have contactId, navigate to detail
                            // In a real app we'd need the Contact ID here
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecentCallItem(
    groupedCall: GroupedCallLog,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val displayName = groupedCall.contactName ?: groupedCall.number
            Text(
                text = if (displayName.isNotEmpty() && displayName.first().isLetter()) displayName.take(1).uppercase() else "#",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Details
        Column(modifier = Modifier.weight(1f)) {
            val displayName = groupedCall.contactName ?: groupedCall.number
            Text(
                text = if (groupedCall.totalCalls > 1) "$displayName (${groupedCall.totalCalls})" else displayName,
                style = MaterialTheme.typography.titleMedium,
                color = if (groupedCall.lastCallType == CallType.MISSED) CallMissedColor else MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (groupedCall.lastCallType) {
                        CallType.INCOMING -> Icons.Filled.CallReceived
                        CallType.OUTGOING -> Icons.Filled.CallMade
                        CallType.MISSED, CallType.REJECTED -> Icons.Filled.CallMissed
                        else -> Icons.Filled.CallReceived
                    },
                    contentDescription = null,
                    tint = when (groupedCall.lastCallType) {
                        CallType.INCOMING -> CallIncomingColor
                        CallType.OUTGOING -> CallOutgoingColor
                        CallType.MISSED, CallType.REJECTED -> CallMissedColor
                        else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                
                val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                Text(
                    text = dateFormat.format(Date(groupedCall.lastCallTimestamp)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Info",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
