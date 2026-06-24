package com.smartdialer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for locally cached contacts.
 */
@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nickname: String? = null,
    val phoneNumbersJson: String = "[]", // JSON array of PhoneNumber
    val emailsJson: String = "[]", // JSON array of strings
    val address: String? = null,
    val company: String? = null,
    val photoUri: String? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val groupName: String? = null,
    val notes: String? = null,
    val birthday: String? = null,
    val deviceContactId: String? = null, // Link to device ContentProvider
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
