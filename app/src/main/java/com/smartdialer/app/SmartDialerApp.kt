package com.smartdialer.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for Smart Dialer.
 * Annotated with @HiltAndroidApp to trigger Hilt code generation.
 */
@HiltAndroidApp
class SmartDialerApp : Application()
