package com.smartdialer.app.presentation.screens.call

import android.telecom.Call
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartdialer.app.presentation.theme.CallIncomingColor
import com.smartdialer.app.presentation.theme.CallMissedColor

@Composable
fun CallScreen(
    onEndCall: () -> Unit,
    viewModel: CallViewModel = hiltViewModel()
) {
    val callState by viewModel.callState.collectAsState()
    val contact by viewModel.contact.collectAsState()
    val number by viewModel.currentNumber.collectAsState()

    // Automatically close the UI if disconnected
    if (callState == Call.STATE_DISCONNECTED) {
        onEndCall()
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.height(48.dp))

            // State Text
            val stateText = when (callState) {
                Call.STATE_RINGING -> "Incoming Call"
                Call.STATE_DIALING -> "Calling..."
                Call.STATE_ACTIVE -> "00:00" // Requires a timer in a real app
                Call.STATE_HOLDING -> "On Hold"
                Call.STATE_DISCONNECTED -> "Call Ended"
                else -> "Connecting..."
            }
            Text(
                text = stateText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Name / Number
            Text(
                text = contact?.name ?: number,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Contact Company or Note if exists
            contact?.company?.takeIf { it.isNotBlank() }?.let { company ->
                Text(
                    text = company,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (contact?.name ?: number).take(1).uppercase(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Controls
            if (callState == Call.STATE_RINGING) {
                // Incoming Call Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CallControlButton(
                        icon = Icons.Filled.CallEnd,
                        label = "Decline",
                        color = CallMissedColor,
                        onClick = { viewModel.rejectCall() }
                    )
                    CallControlButton(
                        icon = Icons.Filled.Call,
                        label = "Answer",
                        color = CallIncomingColor,
                        onClick = { viewModel.answerCall() }
                    )
                }
            } else {
                // Active Call Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CallOptionButton(icon = Icons.Filled.MicOff, label = "Mute", onClick = { })
                    CallOptionButton(icon = Icons.Filled.Dialpad, label = "Keypad", onClick = { })
                    CallOptionButton(icon = Icons.Filled.VolumeUp, label = "Speaker", onClick = { })
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                CallControlButton(
                    icon = Icons.Filled.CallEnd,
                    label = "",
                    color = CallMissedColor,
                    onClick = { viewModel.disconnectCall() }
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun CallControlButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = color,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp))
        }
        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun CallOptionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
