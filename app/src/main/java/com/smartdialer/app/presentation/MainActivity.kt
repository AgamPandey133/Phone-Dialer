package com.smartdialer.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.smartdialer.app.presentation.theme.SmartDialerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            SmartDialerTheme {
                PermissionGate {
                    // Trigger sync once permissions are granted
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        viewModel.initialSync()
                    }
                    MainScreen()
                }
            }
        }
    }
}
