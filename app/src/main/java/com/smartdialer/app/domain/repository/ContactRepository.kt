package com.smartdialer.app.domain.repository

import com.smartdialer.app.domain.model.Contact
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for Contact operations.
 * Implementations handle device contacts, local Room cache, and remote sync.
 */
interface ContactRepository {

    /** Observe all contacts, sorted alphabetically. */
    fun getAllContacts(): Flow<List<Contact>>

    /** Observe favorite contacts. */
    fun getFavoriteContacts(): Flow<List<Contact>>

    /** Get a single contact by ID. */
    suspend fun getContactById(id: Long): Contact?

    /** Search contacts by name, number, nickname, or company. */
    fun searchContacts(query: String): Flow<List<Contact>>

    /** T9 search: match contacts by dial pad key sequence. */
    fun searchByT9(t9Query: String): Flow<List<Contact>>

    /** Add a new contact. */
    suspend fun addContact(contact: Contact): Long

    /** Update an existing contact. */
    suspend fun updateContact(contact: Contact)

    /** Delete a contact. */
    suspend fun deleteContact(contactId: Long)

    /** Toggle favorite status. */
    suspend fun toggleFavorite(contactId: Long, isFavorite: Boolean)

    /** Get contacts by group. */
    fun getContactsByGroup(group: String): Flow<List<Contact>>

    /** Sync contacts from device ContactsProvider. */
    suspend fun syncFromDevice()
}
