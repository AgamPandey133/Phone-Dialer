package com.smartdialer.app.presentation.navigation

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    data object Keypad : Screen("keypad")
    data object Recent : Screen("recent")
    data object Contacts : Screen("contacts")
    data object Favorites : Screen("favorites")
    data object ContactDetail : Screen("contact_detail/{contactId}") {
        fun createRoute(contactId: Long) = "contact_detail/$contactId"
    }
    data object AddEditContact : Screen("add_edit_contact?contactId={contactId}") {
        fun createRoute(contactId: Long? = null) =
            if (contactId != null) "add_edit_contact?contactId=$contactId"
            else "add_edit_contact"
    }
    data object Analytics : Screen("analytics")
    data object Settings : Screen("settings")
    data object Search : Screen("search")
}
