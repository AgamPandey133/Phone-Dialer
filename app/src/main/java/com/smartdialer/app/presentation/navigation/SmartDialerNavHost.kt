package com.smartdialer.app.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartdialer.app.presentation.screens.contacts.ContactsScreen
import com.smartdialer.app.presentation.screens.contacts.ContactDetailScreen
import com.smartdialer.app.presentation.screens.contacts.AddEditContactScreen
import com.smartdialer.app.presentation.screens.favorites.FavoritesScreen
import com.smartdialer.app.presentation.screens.keypad.KeypadScreen
import com.smartdialer.app.presentation.screens.recent.RecentCallsScreen

/**
 * Main navigation graph for the app.
 */
@Composable
fun SmartDialerNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Keypad.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { 100 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { 100 },
                animationSpec = tween(300)
            )
        }
    ) {
        // Bottom Navigation Destinations
        composable(Screen.Keypad.route) {
            KeypadScreen(
                onNavigateToContactDetail = { contactId ->
                    navController.navigate(Screen.ContactDetail.createRoute(contactId))
                }
            )
        }

        composable(Screen.Recent.route) {
            RecentCallsScreen(
                onNavigateToContactDetail = { contactId ->
                    navController.navigate(Screen.ContactDetail.createRoute(contactId))
                }
            )
        }

        composable(Screen.Contacts.route) {
            ContactsScreen(
                onNavigateToContactDetail = { contactId ->
                    navController.navigate(Screen.ContactDetail.createRoute(contactId))
                },
                onNavigateToAddContact = {
                    navController.navigate(Screen.AddEditContact.createRoute())
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateToContactDetail = { contactId ->
                    navController.navigate(Screen.ContactDetail.createRoute(contactId))
                }
            )
        }

        // Detail Screens
        composable(
            route = Screen.ContactDetail.route,
            arguments = listOf(
                navArgument("contactId") { type = NavType.LongType }
            )
        ) {
            ContactDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { contactId ->
                    navController.navigate(Screen.AddEditContact.createRoute(contactId))
                }
            )
        }

        composable(
            route = Screen.AddEditContact.route,
            arguments = listOf(
                navArgument("contactId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            AddEditContactScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
