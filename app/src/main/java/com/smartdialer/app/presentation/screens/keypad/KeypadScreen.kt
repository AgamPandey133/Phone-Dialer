package com.smartdialer.app.presentation.screens.keypad

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telecom.TelecomManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartdialer.app.domain.model.Contact
import com.smartdialer.app.presentation.theme.CallIncomingColor

@Composable
fun KeypadScreen(
    onNavigateToContactDetail: (Long) -> Unit,
    viewModel: KeypadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var dialNumber by remember { mutableStateOf("") }
    val t9Results by viewModel.t9Results.collectAsState()

    // Update T9 search whenever the dialed number changes
    LaunchedEffect(dialNumber) {
        viewModel.onDialNumberChanged(dialNumber)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // T9 Search Results
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.TopStart
        ) {
            if (dialNumber.isNotEmpty() && t9Results.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(t9Results) { contact ->
                        T9ResultItem(
                            contact = contact,
                            onClick = { onNavigateToContactDetail(contact.id) }
                        )
                    }
                }
            }
        }

        // Displayed Number
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dialNumber,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
        }

        // Dial Pad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DialButton(number = "1", letters = "", onClick = { dialNumber += "1" })
                DialButton(number = "2", letters = "ABC", onClick = { dialNumber += "2" })
                DialButton(number = "3", letters = "DEF", onClick = { dialNumber += "3" })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DialButton(number = "4", letters = "GHI", onClick = { dialNumber += "4" })
                DialButton(number = "5", letters = "JKL", onClick = { dialNumber += "5" })
                DialButton(number = "6", letters = "MNO", onClick = { dialNumber += "6" })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DialButton(number = "7", letters = "PQRS", onClick = { dialNumber += "7" })
                DialButton(number = "8", letters = "TUV", onClick = { dialNumber += "8" })
                DialButton(number = "9", letters = "WXYZ", onClick = { dialNumber += "9" })
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DialButton(number = "*", letters = "", onClick = { dialNumber += "*" })
                DialButton(number = "0", letters = "+", onClick = { dialNumber += "0" })
                DialButton(number = "#", letters = "", onClick = { dialNumber += "#" })
            }

            // Call and Backspace buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Empty space to balance
                Box(modifier = Modifier.size(72.dp))

                // Call Button
                FloatingActionButton(
                    onClick = {
                        if (dialNumber.isNotEmpty()) {
                            makeCall(context, dialNumber)
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    containerColor = CallIncomingColor,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Filled.Call,
                        contentDescription = "Call",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Backspace Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .clickable(enabled = dialNumber.isNotEmpty()) {
                            if (dialNumber.isNotEmpty()) {
                                dialNumber = dialNumber.dropLast(1)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (dialNumber.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun T9ResultItem(contact: Contact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.take(1).uppercase(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (contact.phoneNumbers.isNotEmpty()) {
                Text(
                    text = contact.phoneNumbers.first().number,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun DialButton(
    number: String,
    letters: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (letters.isNotEmpty()) {
                Text(
                    text = letters,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

private fun makeCall(context: Context, number: String) {
    try {
        val uri = Uri.fromParts("tel", number, null)
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        telecomManager.placeCall(uri, null)
    } catch (e: SecurityException) {
        // Permission not granted — fallback to ACTION_CALL intent
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        context.startActivity(intent)
    }
}
