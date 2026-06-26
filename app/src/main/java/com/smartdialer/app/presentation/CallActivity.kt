package com.smartdialer.app.presentation

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.smartdialer.app.presentation.screens.call.CallScreen
import com.smartdialer.app.presentation.theme.SmartDialerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * A dedicated Activity for the active/incoming call UI.
 * This needs to be a separate activity so it can use flags to turn on the screen
 * and show over the lock screen without exposing the rest of the app.
 */
@AndroidEntryPoint
class CallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()

        // Critical flags for a dialer app to wake up the screen and show over the lockscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        setContent {
            SmartDialerTheme {
                CallScreen(
                    onEndCall = { finishAndRemoveTask() }
                )
            }
        }
    }
}
