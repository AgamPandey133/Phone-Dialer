package com.smartdialer.app.presentation.screens.keypad

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartdialer.app.presentation.theme.CallIncomingColor

@Composable
fun KeypadScreen(
    onNavigateToContactDetail: (Long) -> Unit
) {
    var dialNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // T9 Search Results Placeholder (takes up remaining space at the top)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (dialNumber.isNotEmpty()) {
                // Here we will show the list of matching contacts
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            text = "Searching contacts for: $dialNumber",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Displayed Number
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
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
                .padding(horizontal = 32.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DialButton(number = "1", letters = "oo", onClick = { dialNumber += "1" })
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
                    onClick = { /* TODO: Initiate Call */ },
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
                val displayText = if (letters == "oo") "∞" else letters
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
