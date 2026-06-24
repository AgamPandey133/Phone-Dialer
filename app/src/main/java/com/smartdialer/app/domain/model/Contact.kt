package com.smartdialer.app.domain.model

/**
 * Domain model representing a contact.
 * This is decoupled from Room entities and API DTOs.
 */
data class Contact(
    val id: Long = 0,
    val name: String,
    val nickname: String? = null,
    val phoneNumbers: List<PhoneNumber> = emptyList(),
    val emails: List<String> = emptyList(),
    val address: String? = null,
    val company: String? = null,
    val photoUri: String? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val group: String? = null,
    val notes: String? = null,
    val birthday: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Represents a phone number with a label (e.g., Mobile, Home, Work).
 */
data class PhoneNumber(
    val number: String,
    val label: PhoneNumberLabel = PhoneNumberLabel.MOBILE
)

enum class PhoneNumberLabel {
    MOBILE, HOME, WORK, FAX, OTHER
}
